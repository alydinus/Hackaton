package kg.aiu.telegram_sevrice.service.serviceImpl;

import kg.aiu.telegram_sevrice.components.TelSessionModel;
import kg.aiu.telegram_sevrice.service.SessionService;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class SessionServiceImpl implements SessionService {
    private final Map<Long, TelSessionModel> userSessions = new ConcurrentHashMap<>();

    public TelSessionModel getSession(Long chatId) {
        return userSessions.computeIfAbsent(chatId, k -> new TelSessionModel());
    }

    public void saveSession(Long chatId, TelSessionModel session) {
        userSessions.put(chatId, session);
    }

    public void clearSession(Long chatId) {
        userSessions.remove(chatId);
    }

    public void cleanupExpiredSessions() {
        userSessions.entrySet().removeIf(entry ->
                entry.getValue().isExpired()
        );
    }
}
