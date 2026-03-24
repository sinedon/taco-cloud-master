package edu.iu.p566.videoScheduler.controllers;

import edu.iu.p566.videoScheduler.data.ScheduleRepository;
import edu.iu.p566.videoScheduler.data.UserRepository;
import edu.iu.p566.videoScheduler.model.Schedule;
import edu.iu.p566.videoScheduler.model.User;
import edu.iu.p566.videoScheduler.security.YoutubeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ScheduleRepository scheduleRepo;

    @MockBean
    private YoutubeService youtubeService;

    private User testUser;

    @BeforeEach
    void setup() {
        scheduleRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        userRepo.save(testUser);
    }

    private Schedule createValidSchedule(User user) {
        LocalDateTime now = LocalDateTime.now().plusMinutes(5);

        Schedule s = new Schedule();
        s.setVideoName("Test");
        s.setYoutubeURL("https://youtube.com?v=test");
        s.setUser(user);
        s.setSchedTime(now);
        s.setDurationSeconds(120L);
        s.setSchedTimeUtc(
            now.atZone(ZoneId.of("America/New_York")).toInstant()
        );

        return s;
    }
    
    @Test
    void testLoginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegisterPageLoads() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testSchedulePageLoads() throws Exception {
        mockMvc.perform(get("/schedule")
                .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule"));
    }

    @Test
    void testScheduleOverlapFails() throws Exception {

        when(youtubeService.getVideoDuration("https://youtube.com?v=test"))
                .thenReturn(120L);

        LocalDateTime start = LocalDateTime.now().plusMinutes(5);

        Schedule s = new Schedule();
        s.setVideoName("Existing");
        s.setYoutubeURL("https://youtube.com?v=test");
        s.setUser(testUser);
        s.setSchedTime(start);
        s.setDurationSeconds(120L);
        s.setSchedTimeUtc(start.atZone(ZoneId.of("America/New_York")).toInstant());

        scheduleRepo.save(s);

        mockMvc.perform(post("/schedule")
                .with(user("testuser"))
                .with(csrf()) 
                .param("videoName", "Overlap")
                .param("youtubeURL", "https://youtube.com?v=test")
                .param("schedTime", start.plusSeconds(30)
                        .withSecond(0)
                        .withNano(0)
                        .toString()) 
                .param("timeZone", "America/New_York"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("schedule"));

        assertEquals(1, scheduleRepo.count()); 
    }

    @Test
    void testDeleteSchedule() throws Exception {

        Schedule s = scheduleRepo.save(createValidSchedule(testUser)); 

        mockMvc.perform(post("/schedule/delete/" + s.getVideoID())
                .with(user("testuser"))
                .with(csrf())) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule"));

        assertEquals(0, scheduleRepo.count());
    }

    @Test
    void testHomeNoVideo() throws Exception {

        mockMvc.perform(get("/")
                .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("video"));
    }

    @Test
    void testVideoLoadsWithOffsetOnRefresh() throws Exception {

        when(youtubeService.getVideoDuration("https://youtube.com?v=test"))
                .thenReturn(120L);

        Schedule s = new Schedule();
        s.setVideoName("Now Playing");
        s.setYoutubeURL("https://youtube.com?v=test");
        s.setUser(testUser);

        LocalDateTime start = LocalDateTime.now().minusSeconds(10);

        s.setSchedTime(start);
        s.setDurationSeconds(120L);
        s.setSchedTimeUtc(start.atZone(ZoneId.of("America/New_York")).toInstant());

        scheduleRepo.save(s);

        mockMvc.perform(get("/")
                .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("video"))
                .andExpect(model().attributeExists("startOffset"));
    }
}