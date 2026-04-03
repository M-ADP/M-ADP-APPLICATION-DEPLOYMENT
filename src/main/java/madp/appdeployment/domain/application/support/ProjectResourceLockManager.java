package madp.appdeployment.domain.application.support;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.ProjectResourceLockConflictException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ProjectResourceLockManager {
    private static final Duration LOCK_TTL = Duration.ofSeconds(30);
    private static final Duration LOCK_WAIT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration RETRY_INTERVAL = Duration.ofMillis(100);
    private static final String LOCK_KEY_PREFIX = "lock:project:resource:";

    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;

    public void executeWithLock(String projectId, Runnable action) {
        executeWithLock(projectId, () -> {
            action.run();
            return null;
        });
    }

    public <T> T executeWithLock(String projectId, Supplier<T> action) {
        String lockKey = LOCK_KEY_PREFIX + projectId;
        String lockValue = UUID.randomUUID().toString();
        long deadline = System.nanoTime() + LOCK_WAIT_TIMEOUT.toNanos();

        while (System.nanoTime() < deadline) {
            Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TTL);
            if (Boolean.TRUE.equals(acquired)) {
                try {
                    return action.get();
                } finally {
                    releaseLock(lockKey, lockValue);
                }
            }

            sleepBeforeRetry();
        }

        throw new ProjectResourceLockConflictException();
    }

    private void releaseLock(String lockKey, String lockValue) {
        try {
            stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        } catch (Exception ignored) {
            // Lock release failure is recovered by TTL expiration.
        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_INTERVAL.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProjectResourceLockConflictException();
        }
    }
}
