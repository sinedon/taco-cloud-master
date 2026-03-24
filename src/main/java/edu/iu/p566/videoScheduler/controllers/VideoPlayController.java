package edu.iu.p566.videoScheduler.controllers;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.iu.p566.videoScheduler.data.ScheduleRepository;
import edu.iu.p566.videoScheduler.model.Schedule;

@RequestMapping("/playVideo")
@Controller
public class VideoPlayController {
    @Autowired
    ScheduleRepository schedRepo;

    @GetMapping()
    public String displayVideo(Model model, Principal principal) {
        String username = principal.getName();
        Optional<Schedule> video = schedRepo.findFirstByUserUsernameAndSchedTimeUtcLessThanEqualOrderBySchedTimeUtcAsc(username, Instant.now());

        if (video.isPresent()) {
            Schedule v = video.get();
            model.addAttribute("video", video.get());
            schedRepo.save(v);
        }

        return("playVideo");
    }
}