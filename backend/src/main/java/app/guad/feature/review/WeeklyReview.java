package app.guad.feature.review;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "weekly_reviews")
public class WeeklyReview {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Instant startedAt;

    @Column
    private Instant completedAt;

    @Column(nullable = false)
    private ReviewStep currentStep;

    @Column
    private String notes;

    @Column(nullable = false)
    private UUID userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public ReviewStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(ReviewStep currentStep) {
        this.currentStep = currentStep;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
