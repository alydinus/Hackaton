package kg.aiu.telegram_sevrice.components;

import kg.aiu.telegram_sevrice.service.OrderServiceClient;
import kg.spring.shared.dto.response.OrderResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderHandler {

    private final OrderServiceClient telService;
    private final TelegramBot bot;

    public OrderHandler(OrderServiceClient telService, TelegramBot bot) {
        this.telService = telService;
        this.bot = bot;
    }

    public void handleOrderResponsesCommand(Long chatId) {
        String message = "📋 *Управление заказами*\n\nВыберите действие:";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("📥 Список заказов", "orders_list"));
        row1.add(createButton("🔍 Поиск заказа", "orders_search"));
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("➕ Создать заказ", "order_create"));
        row2.add(createButton("📊 Обновить заказ", "orders_update"));
        row2.add(createButton("❌ Удалить заказ", "orders_delete"));
        rows.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("📋 Главное меню", "main_menu"));
        rows.add(row3);

        keyboard.setKeyboard(rows);

        bot.sendMessageWithKeyboard(chatId, message, keyboard);
    }

    public void showOrderResponsesList(Long chatId, int page) {
        try {
            List<OrderResponse> orders = telService.getAllOrderResponses();

            if (orders.isEmpty()) {
                bot.sendTextMessage(chatId, "📭 Заказы не найдены");
                return;
            }

            // Пагинация
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) orders.size() / pageSize);
            int start = page * pageSize;
            int end = Math.min(start + pageSize, orders.size());

            StringBuilder message = new StringBuilder("📋 *Список заказов* (стр. " + (page + 1) + "/" + totalPages + "):\n\n");

            for (int i = start; i < end; i++) {
                OrderResponse order = orders.get(i);
                String statusEmoji = getStatusEmoji(order.status());
                message.append(String.format(
                        "%s *ID:* %d\n👤 *Клиент:* %s\n📦 *Товар:* %s\n💵 *Сумма:* %s ₽\n\n",
                        statusEmoji, order.id(), order.customerId(),
                        order.productId()
                ));
            }

            InlineKeyboardMarkup keyboard = createOrderResponsesPaginationKeyboard(page, totalPages);
            bot.sendMessageWithKeyboard(chatId, message.toString(), keyboard);

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при получении заказов: " + e.getMessage());
        }
    }

    public void showOrderResponseDetails(Long chatId, Long orderId) {
        try {
            OrderResponse order = telService.getOrderResponseById(orderId);
            String statusEmoji = getStatusEmoji(order.status());

            String message = String.format(
                    "📄 *Детали заказа:*\n\n" +
                            "%s *ID:* %d\n" +
                            "👤 *Клиент:* %s\n" +
                            "📦 *Товар:* %s\n" +
                            "💵 *Сумма:* %s ₽\n" +
                            "📊 *Статус:* %s\n" +
                            "⏰ *Создан:* %s",
                    statusEmoji, order.id(), order.customerId(), order.productId()
                    , order.status(),
                    order.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            row1.add(createButton("✏️ Редактировать", "order_edit_" + orderId));
            row1.add(createButton("🗑️ Удалить", "order_delete_" + orderId));
            rows.add(row1);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            row2.add(createButton("✅ Завершить", "order_complete_" + orderId));
            row2.add(createButton("❌ Отменить", "order_cancel_" + orderId));
            rows.add(row2);

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            row3.add(createButton("📋 Назад к списку", "orders_list"));
            rows.add(row3);

            keyboard.setKeyboard(rows);

            bot.sendMessageWithKeyboard(chatId, message, keyboard);

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при получении заказа: " + e.getMessage());
        }
    }

    public void startOrderResponseCreation(Long chatId, TelSessionModel session) {
        session.setState(TelSessionModel.BotState.AWAITING_ORDER_CUSTOMER);
        session.setCurrentFlow("ORDER_CREATION");
        session.getContext().clear();

        bot.sendTextMessage(chatId, "🛒 *Создание нового заказа*\n\nВведите имя клиента:");
    }

    public void processOrderResponseCreation(Long chatId, String text, TelSessionModel session) {
        switch (session.getState()) {
            case AWAITING_ORDER_CUSTOMER:
                session.getContext().put("customerName", text);
                session.setState(TelSessionModel.BotState.AWAITING_ORDER_PRODUCT_SELECTION);
                showProductResponsesForOrderResponse(chatId);
                break;

            case AWAITING_ORDER_QUANTITY:
                try {
                    int quantity = Integer.parseInt(text);
                    session.getContext().put("quantity", quantity);
                    completeOrderResponseCreation(chatId, session);
                } catch (NumberFormatException e) {
                    bot.sendTextMessage(chatId, "❌ Неверный формат количества. Введите число:");
                }
                break;
        }
    }

    private void showProductResponsesForOrderResponse(Long chatId) {
        try {
            List<com.example.crm.model.ProductResponse> products = telService.getAllProductResponses();

            if (products.isEmpty()) {
                bot.sendTextMessage(chatId, "📭 Нет доступных товаров для заказа");
                return;
            }

            String message = "📦 *Выберите товар для заказа:*";

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (com.example.crm.model.ProductResponse product : products) {
                if (product.getStockQuantity() > 0) {
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    String buttonText = String.format("%s - %s ₽", product.getName(), product.getPrice());
                    row.add(createButton(buttonText, "select_product_" + product.getId()));
                    rows.add(row);
                }
            }

            List<InlineKeyboardButton> backRow = new ArrayList<>();
            backRow.add(createButton("📋 Отмена", "main_menu"));
            rows.add(backRow);

            keyboard.setKeyboard(rows);

            bot.sendMessageWithKeyboard(chatId, message, keyboard);

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при получении товаров: " + e.getMessage());
        }
    }

    public void searchOrderResponses(Long chatId, String query) {
        try {
            List<OrderResponse> orders = telService.searchOrderResponses(query);

            if (orders.isEmpty()) {
                bot.sendTextMessage(chatId, "🔍 Заказы по запросу '" + query + "' не найдены");
                return;
            }

            StringBuilder message = new StringBuilder("🔍 *Результаты поиска заказов:* '" + query + "'\n\n");

            for (OrderResponse order : orders) {
                String statusEmoji = getStatusEmoji(order.status());
                message.append(String.format(
                        "%s *ID:* %d\n👤 *Клиент:* %s\n📦 *Товар:* %s\n💵 *Сумма:* %s ₽\n\n",
                        statusEmoji, order.id(), order.customerId(),
                        order.productId()
                ));
            }

            bot.sendTextMessage(chatId, message.toString());

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при поиске заказов: " + e.getMessage());
        }
    }

    private void completeOrderResponseCreation(Long chatId, TelSessionModel session) {
        try {
            String customerName = (String) session.getContext().get("customerName");
            Long productId = (Long) session.getContext().get("selectedProductResponseId");
            Integer quantity = (Integer) session.getContext().get("quantity");

            com.example.crm.model.ProductResponse product = telService.getProductResponseById(productId);

            OrderResponse order = new OrderResponse();
            order.setCustomerName(customerName);
            order.setProductResponse(product.getName());
            order.setAmount(product.getPrice().multiply(new BigDecimal(quantity)));
            order.setStatus(OrderResponseStatus.NEW);
            order.setCreatedAt(java.time.LocalDateTime.now());

            OrderResponse createdOrderResponse = telService.createOrderResponse(order);

            String message = String.format(
                    "✅ *Заказ успешно создан!*\n\n" +
                            "🆔 ID: %d\n" +
                            "👤 Клиент: %s\n" +
                            "📦 Товар: %s\n" +
                            "🔢 Количество: %d шт.\n" +
                            "💵 Сумма: %s ₽\n" +
                            "📊 Статус: %s",
                    createdOrderResponse.getId(), createdOrderResponse.getCustomerName(), createdOrderResponse.getProductResponse(),
                    quantity, createdOrderResponse.getAmount(), createdOrderResponse.getStatus()
            );

            bot.sendTextMessage(chatId, message);
            session.reset();

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при создании заказа: " + e.getMessage());
            session.reset();
        }
    }

    private String getStatusEmoji(OrderResponseStatus status) {
        switch (status) {
            case NEW: return "🆕";
            case IN_PROCESS: return "🔄";
            case COMPLETED: return "✅";
            case CANCELLED: return "❌";
            default: return "📋";
        }
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private InlineKeyboardMarkup createOrderResponsesPaginationKeyboard(int currentPage, int totalPages) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопки навигации
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        if (currentPage > 0) {
            navRow.add(createButton("⬅️ Назад", "orders_list_" + (currentPage - 1)));
        }

        if (currentPage < totalPages - 1) {
            navRow.add(createButton("Вперед ➡️", "orders_list_" + (currentPage + 1)));
        }

        if (!navRow.isEmpty()) {
            rows.add(navRow);
        }

        // Кнопка обновления
        List<InlineKeyboardButton> refreshRow = new ArrayList<>();
        refreshRow.add(createButton("🔄 Обновить", "orders_list_" + currentPage));
        rows.add(refreshRow);

        // Кнопка возврата
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(createButton("📋 Управление заказами", "orders_list"));
        backRow.add(createButton("📋 Главное меню", "main_menu"));
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}