package edu.iu.p566.videoScheduler.model;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long videoID;

    @NotNull
    private String videoName;

    @NotNull
    private String youtubeURL;

    @NotNull
    private LocalDateTime schedTime;

    private Instant schedTimeUtc;

    private String timeZone;

    @NotNull
    private Long durationSeconds;

    private boolean played = false;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
}
