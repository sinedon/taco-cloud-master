package edu.iu.p566.videoScheduler.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.iu.p566.videoScheduler.data.ScheduleRepository;
import edu.iu.p566.videoScheduler.data.UserRepository;
import edu.iu.p566.videoScheduler.model.Schedule;
import edu.iu.p566.videoScheduler.model.User;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleRepository scheduleRepo;
    private final UserRepository userRepo;

    @GetMapping()
    public String displaySchedule(Model model, Principal principal) {
        String username = principal.getName();

        Optional<Schedule> dueVideo =
            scheduleRepo.findFirstByUserUsernameAndSchedTimeLessThanEqualAndPlayedFalseOrderBySchedTimeAsc(
                username,
                LocalDateTime.now()
            );

        if (dueVideo.isPresent()) {
            return "redirect:/";
        }

        List<Schedule> schedules = scheduleRepo.findByUserUsername(username);

        model.addAttribute("schedules", schedules);
        model.addAttribute("username", username);
        model.addAttribute("schedule", new Schedule());

        return "schedule";
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, Principal principal) {

        Optional<Schedule> scheduleOpt = scheduleRepo.findById(id);

        if(scheduleOpt.isPresent()) {

            Schedule sched = scheduleOpt.get();

            if(sched.getUser().getUsername().equals(principal.getName())) {
                scheduleRepo.deleteById(id);
            }
        }

        return "redirect:/schedule";
    }
    @PostMapping()
    public String saveSchedule(@ModelAttribute Schedule schedule, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username);
        schedule.setUser(user);
        scheduleRepo.save(schedule);
        return "redirect:/schedule";
    }
}
