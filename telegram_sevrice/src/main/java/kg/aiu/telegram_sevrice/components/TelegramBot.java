package kg.aiu.telegram_sevrice.components;

import kg.aiu.telegram_sevrice.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SessionService sessionService;
    private final ProductHandler productHandlers;
    private final OrderHandler orderHandlers;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                          SessionService sessionService,
                          ProductHandler productHandlers,
                          OrderHandler orderHandlers) {
        super(botToken);
        this.sessionService = sessionService;
        this.productHandlers = productHandlers;
        this.orderHandlers = orderHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        TelSessionModel session = sessionService.getSession(chatId);

        log.info("Received message from " + chatId + ": " + text + " (state: " + session.getState() + ")");

        if (session.getState() != TelSessionModel.BotState.IDLE) {
            handleStateMessage(chatId, text, session);
            sessionService.saveSession(chatId, session);
            return;
        }

        switch (text) {
            case "📋 Главное меню":

                break;
            case "Назад":
                sendMainMenu(chatId);
                break;
            case "📦 Товары":
                productHandlers.handleProductResponsesCommand(chatId);
                break;
            case "📋 Заказы":
                orderHandlers.handleOrderResponsesCommand(chatId);
                break;
            case "🆕 Новый товар":
                productHandlers.startProductResponseCreation(chatId, session);
                sessionService.saveSession(chatId, session);
                break;
            case "➕ Новый заказ":
                orderHandlers.startOrderResponseCreation(chatId, session);
                sessionService.saveSession(chatId, session);
                break;
            case "📊 Статистика":
                showStatistics(chatId);
                break;
            case "❓ Помощь":
                sendHelpMessage(chatId);
                break;
            default:
                sendHelpMessage(chatId);
                break;
        }

        sessionService.saveSession(chatId, session);
    }

    private void handleStateMessage(Long chatId, String text, TelSessionModel session) {
        switch (session.getState()) {
            case AWAITING_PRODUCT_NAME:
            case AWAITING_PRODUCT_DESCRIPTION:
            case AWAITING_PRODUCT_PRICE:
            case AWAITING_PRODUCT_STOCK:
            case AWAITING_PRODUCT_CATEGORY:
                productHandlers.processProductResponseCreation(chatId, text, session);
                break;
            case AWAITING_ORDER_CUSTOMER:
            case AWAITING_ORDER_QUANTITY:
                orderHandlers.processOrderResponseCreation(chatId, text, session);
                break;
            case SEARCHING_PRODUCTS:
                productHandlers.searchProductResponses(chatId, text);
                session.setState(TelSessionModel.BotState.IDLE);
                break;
            case SEARCHING_ORDERS:
                orderHandlers.searchOrderResponses(chatId, text);
                session.setState(TelSessionModel.BotState.IDLE);
                break;
            default:
                sendTextMessage(chatId, "Неизвестное состояние. Возврат в главное меню.");
                session.reset();
                sendMainMenu(chatId);
                break;
        }
    }

    private void handleCallback(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Message message = callbackQuery.getMessage();

        System.out.println("Received callback from " + chatId + ": " + data);

        try {
            if (data.startsWith("products_list_")) {
                int page = Integer.parseInt(data.split("_")[2]);
                productHandlers.showProductResponsesList(chatId, page);
            } else if (data.startsWith("product_detail_")) {
                Long productId = Long.parseLong(data.split("_")[2]);
                productHandlers.showProductResponseDetails(chatId, productId);
            } else if (data.startsWith("product_category_")) {
                String category = data.substring("product_category_".length());
                TelSessionModel session = sessionService.getSession(chatId);
                session.getContext().put("category", category);
                productHandlers.completeProductResponseCreation(chatId, session);
                sessionService.saveSession(chatId, session);
            } else if (data.startsWith("product_edit_")) {
                Long productId = Long.parseLong(data.split("_")[2]);
                productHandlers.startProductResponseEditing(chatId, productId);
            } else if (data.startsWith("product_delete_")) {
                Long productId = Long.parseLong(data.split("_")[2]);
                productHandlers.deleteProductResponse(chatId, productId);
            } else if (data.equals("product_create")) {
                TelSessionModel session = sessionService.getSession(chatId);
                productHandlers.startProductResponseCreation(chatId, session);
                sessionService.saveSession(chatId, session);
            } else if (data.equals("products_search")) {
                TelSessionModel session = sessionService.getSession(chatId);
                session.setState(TelSessionModel.BotState.SEARCHING_PRODUCTS);
                sendTextMessage(chatId, "🔍 Введите название товара для поиска:");
                sessionService.saveSession(chatId, session);
            } else if (data.equals("orders_list")) {
                orderHandlers.showOrderResponsesList(chatId, 0);
            } else if (data.equals("order_create")) {
                TelSessionModel session = sessionService.getSession(chatId);
                orderHandlers.startOrderResponseCreation(chatId, session);
                sessionService.saveSession(chatId, session);
            } else if (data.startsWith("order_detail_")) {
                Long orderId = Long.parseLong(data.split("_")[2]);
                orderHandlers.showOrderResponseDetails(chatId, orderId);
            } else if (data.equals("main_menu")) {
                sendMainMenu(chatId);
            } else if (data.equals("refresh_products")) {
                productHandlers.showProductResponsesList(chatId, 0);
            } else if (data.equals("refresh_orders")) {
                orderHandlers.showOrderResponsesList(chatId, 0);
            } else {
                sendTextMessage(chatId, "❌ Неизвестная команда: " + data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendTextMessage(chatId, "❌ Ошибка обработки запроса: " + e.getMessage());
        }
    }

    public void sendMainMenu(Long chatId) {
        String message = "🏪 *Главное меню CRM*\n\nВыберите раздел для управления:";

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        // Первый ряд
        KeyboardRow row1 = new KeyboardRow();
        row1.add("📦 Товары");
        row1.add("📋 Заказы");
        rows.add(row1);

        // Второй ряд
        KeyboardRow row2 = new KeyboardRow();
        row2.add("🆕 Новый товар");
        row2.add("➕ Новый заказ");
        rows.add(row2);

        // Третий ряд
        KeyboardRow row3 = new KeyboardRow();
        row3.add("📊 Статистика");
        row3.add("❓ Помощь");
        rows.add(row3);

        keyboard.setKeyboard(rows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setParseMode("Markdown");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showStatistics(Long chatId) {
        // Здесь можно добавить реальную статистику
        String message = "📊 *Статистика*\n\n" +
                "📦 Товаров: 15\n" +
                "📋 Заказов: 47\n" +
                "💵 Выручка: 1,234,500 ₽\n" +
                "👥 Клиентов: 28";

        sendTextMessage(chatId, message);
    }

    private void sendHelpMessage(Long chatId) {
        String message = "❓ *Помощь*\n\n" +
                "🏪 *Главное меню* - основное меню управления\n" +
                "📦 *Товары* - управление товарами\n" +
                "📋 *Заказы* - управление заказами\n" +
                "🆕 *Новый товар* - создание нового товара\n" +
                "➕ *Новый заказ* - создание нового заказа\n\n" +
                "Для начала работы используйте кнопки меню или команды.";

        sendTextMessage(chatId, message);
    }

    public void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}