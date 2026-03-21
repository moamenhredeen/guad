package app.guad.feature.profile;

import app.guad.core.AuditMetadata;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID keycloakId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String timezone = "Europe/Berlin";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek defaultReviewDay = DayOfWeek.SATURDAY;

    @Column(nullable = false)
    private boolean energyTrackingEnabled = true;

    @Column(nullable = false)
    private boolean emailDigestsEnabled = false;

    @Column(nullable = false)
    private boolean reminderNotificationsEnabled = true;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(UUID keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public DayOfWeek getDefaultReviewDay() {
        return defaultReviewDay;
    }

    public void setDefaultReviewDay(DayOfWeek defaultReviewDay) {
        this.defaultReviewDay = defaultReviewDay;
    }

    public boolean isEnergyTrackingEnabled() {
        return energyTrackingEnabled;
    }

    public void setEnergyTrackingEnabled(boolean energyTrackingEnabled) {
        this.energyTrackingEnabled = energyTrackingEnabled;
    }

    public boolean isEmailDigestsEnabled() {
        return emailDigestsEnabled;
    }

    public void setEmailDigestsEnabled(boolean emailDigestsEnabled) {
        this.emailDigestsEnabled = emailDigestsEnabled;
    }

    public boolean isReminderNotificationsEnabled() {
        return reminderNotificationsEnabled;
    }

    public void setReminderNotificationsEnabled(boolean reminderNotificationsEnabled) {
        this.reminderNotificationsEnabled = reminderNotificationsEnabled;
    }

    public AuditMetadata getAudit() {
        return audit;
    }

    public void setAudit(AuditMetadata audit) {
        this.audit = audit;
    }
}
