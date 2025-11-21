package kg.aiu.telegram_sevrice.service;

import kg.aiu.telegram_sevrice.components.TelSessionModel;

public interface SessionService {
     TelSessionModel getSession(Long chatId);

     void saveSession(Long chatId, TelSessionModel session);

     void clearSession(Long chatId);

     void cleanupExpiredSessions();
}
