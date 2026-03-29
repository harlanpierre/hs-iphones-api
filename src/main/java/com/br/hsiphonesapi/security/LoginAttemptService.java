package com.br.hsiphonesapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutos

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        attempts.compute(username, (key, info) -> {
            if (info == null || isExpired(info)) {
                return new AttemptInfo(1, System.currentTimeMillis());
            }
            int newCount = info.count + 1;
            if (newCount >= MAX_ATTEMPTS) {
                log.warn("Conta bloqueada por excesso de tentativas: '{}'", username);
            }
            return new AttemptInfo(newCount, System.currentTimeMillis());
        });
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return false;
        if (isExpired(info)) {
            attempts.remove(username);
            return false;
        }
        return info.count >= MAX_ATTEMPTS;
    }

    public long getRemainingLockTimeSeconds(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return 0;
        long elapsed = System.currentTimeMillis() - info.lastAttempt;
        long remaining = LOCK_DURATION_MS - elapsed;
        return remaining > 0 ? remaining / 1000 : 0;
    }

    private boolean isExpired(AttemptInfo info) {
        return System.currentTimeMillis() - info.lastAttempt > LOCK_DURATION_MS;
    }

    private record AttemptInfo(int count, long lastAttempt) {}
}
