package com.fuzentrix.backend.service;

import com.fuzentrix.backend.entity.UserSession;
import com.fuzentrix.backend.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Scheduled service that periodically purges expired and revoked sessions from the database.
 *
 * <p>The job is <b>disabled by default</b>. Enable it by setting the following property:
 * <pre>app.session.cleanup.scheduled.enabled=true</pre>
 *
 * <p>Per-user cleanup on login is always active regardless of this setting
 * (see {@link com.fuzentrix.backend.service.AuthService}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionCleanupService {

    private final UserSessionRepository sessionRepository;

    /**
     * Runs weekly (every Sunday at 02:00) when enabled via
     * {@code app.session.cleanup.scheduled.enabled=true}.
     *
     * <p>The {@code condition} attribute ensures the @Scheduled annotation is only
     * activated when the property is explicitly set to {@code true}. Defaults to disabled.
     */
    @Scheduled(
            cron = "${app.session.cleanup.cron:0 0 2 * * SUN}",
            zone = "UTC"
    )
    @Transactional
    public void purgeExpiredAndRevokedSessions() {
        if (!isScheduledCleanupEnabled()) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        log.info("Running scheduled session cleanup job at {}", now);
        sessionRepository.deleteAllExpiredOrRevokedSessions(now);
        log.info("Scheduled session cleanup complete.");
    }

    /**
     * Reads the enabled flag at runtime so no restart is needed to toggle it
     * (when using a config server or env var refresh).
     */
    private boolean isScheduledCleanupEnabled() {
        String enabled = System.getProperty("app.session.cleanup.scheduled.enabled",
                System.getenv().getOrDefault("APP_SESSION_CLEANUP_ENABLED", "false"));
        return Boolean.parseBoolean(enabled);
    }
}
