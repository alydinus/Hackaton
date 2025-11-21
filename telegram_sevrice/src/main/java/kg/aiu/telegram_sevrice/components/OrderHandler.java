package kg.aiu.telegram_sevrice.components;

import kg.aiu.telegram_sevrice.service.OrderServiceClient;
import kg.aiu.telegram_sevrice.service.ProductServiceClient;
import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.response.OrderResponse;
import kg.spring.shared.dto.response.ProductResponse;
import kg.spring.shared.enums.OrderStatus;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderHandler {

    private final OrderServiceClient orderService;
    private final ProductServiceClient productService;
    private final TelegramBot bot;

    public OrderHandler(OrderServiceClient orderService, ProductServiceClient productService, TelegramBot bot) {
        this.orderService = orderService;
        this.productService = productService;
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
            List<OrderResponse> orders = orderService.getAllOrderResponses();

            if (orders.isEmpty()) {
                bot.sendTextMessage(chatId, "📭 Заказы не найдены");
                return;
            }

            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) orders.size() / pageSize);
            int start = page * pageSize;
            int end = Math.min(start + pageSize, orders.size());

            StringBuilder message = new StringBuilder("📋 *Список заказов* (стр. " + (page + 1) + "/" + totalPages + "):\n\n");

            for (int i = start; i < end; i++) {
                OrderResponse order = orders.get(i);
                String statusEmoji = getStatusEmoji(order.status());
                message.append(String.format(
                        "%s *ID:* %d\n👤 *Клиент:* %s\n📦 *Товары:* %s\n💵 *Сумма:* %s ₽\n\n",
                        statusEmoji, order.id(), order.customer().id(),
                        getProductIdsFromOrder(order.products())
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
            OrderResponse order = orderService.getOrderResponseById(orderId);
            String statusEmoji = getStatusEmoji(order.status());

            String message = String.format(
                    "📄 *Детали заказа:*\n\n" +
                            "%s *ID:* %d\n" +
                            "👤 *Клиент:* %s\n" +
                            "📦 *Товары:* %s\n" +
                            "📊 *Статус:* %s\n" +
                            "⏰ *Создан:* %s",
                    statusEmoji, order.id(), order.customer().id(), getProductIdsFromOrder(order.products())
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
            List<ProductResponse> products = productService.getAllProductResponses();

            if (products.isEmpty()) {
                bot.sendTextMessage(chatId, "📭 Нет доступных товаров для заказа");
                return;
            }

            String message = "📦 *Выберите товар для заказа:*";

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (ProductResponse product : products) {
                if(product.quantity() > 0){
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    String buttonText = String.format("%s - %s ₽", product.name(), product.price());
                    row.add(createButton(buttonText, "select_product_" + product.id()));
                    rows.add(row);
                } else{
                    //add here code later
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

//    public void searchOrderResponses(Long chatId, String query) {
//        try {
//            List<OrderResponse> orders = orderService.searchOrderResponses(query);
//
//            if (orders.isEmpty()) {
//                bot.sendTextMessage(chatId, "🔍 Заказы по запросу '" + query + "' не найдены");
//                return;
//            }
//
//            StringBuilder message = new StringBuilder("🔍 *Результаты поиска заказов:* '" + query + "'\n\n");
//
//            for (OrderResponse order : orders) {
//                String statusEmoji = getStatusEmoji(order.status());
//                message.append(String.format(
//                        "%s *ID:* %d\n👤 *Клиент:* %s\n📦 *Товар:* %s\n💵 *Сумма:* %s ₽\n\n",
//                        statusEmoji, order.id(), order.customerId(),
//                        order.productId()
//                ));
//            }
//
//            bot.sendTextMessage(chatId, message.toString());
//
//        } catch (Exception e) {
//            bot.sendTextMessage(chatId, "❌ Ошибка при поиске заказов: " + e.getMessage());
//        }
//    }

    private void completeOrderResponseCreation(Long chatId, TelSessionModel session) {
        try {
//            String customerName = (String) session.getContext().get("customerName");
            //Just username for later
            Long customerId = (Long)session.getContext().get("customerName");
            Long productId = (Long) session.getContext().get("selectedProductResponseId");
            Integer quantity = (Integer) session.getContext().get("quantity");

            ProductResponse product = productService.getProductResponseById(productId);
            List<Long> productIds = new ArrayList<>();
            //add many products later
            productIds.add(productId);

            CreateOrderRequest order = new CreateOrderRequest(
                    customerId,
                    productIds,
                    quantity,
                    OrderStatus.UNPAID,
                    LocalDateTime.now(),
                    getPrice(productIds)
                    );

            OrderResponse createdOrderResponse = orderService.createOrder(order);

            String message = String.format(
                    "✅ *Заказ успешно создан!*\n\n" +
                            "🆔 ID: %d\n" +
                            "👤 Клиент: %s\n" +
                            "📦 Товары: %s\n" +
                            "🔢 Количество: %d шт.\n" +
                            "💵 Сумма: %s ₽\n" +
                            "📊 Статус: %s",
                    createdOrderResponse.id(), createdOrderResponse.customer().name(),
                    getProductIdsFromOrder(createdOrderResponse.products()),
                    quantity, order.price(), createdOrderResponse.status()
            );

            bot.sendTextMessage(chatId, message);
            session.reset();

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при создании заказа: " + e.getMessage());
            session.reset();
        }
    }

    private String getStatusEmoji(OrderStatus status) {
        switch (status) {
            case OrderStatus.UNPAID: return "🆕";
            case OrderStatus.RETURN: return "🔄";
            case OrderStatus.PAID: return "✅";
            case OrderStatus.CANCELED: return "❌";
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

        List<InlineKeyboardButton> refreshRow = new ArrayList<>();
        refreshRow.add(createButton("🔄 Обновить", "orders_list_" + currentPage));
        rows.add(refreshRow);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(createButton("📋 Управление заказами", "orders_list"));
        backRow.add(createButton("📋 Главное меню", "main_menu"));
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private String getProductIdsFromOrder(List<ProductResponse> responses) {
        StringBuilder productIds = new StringBuilder();
      for(ProductResponse response : responses) {
          productIds.append(response.id());
          productIds.append(", ");
      }
      return productIds.toString();
    };

    private Double getPrice(List<Long> responseIds) {
        Double d = 0.0;
        for(Long id : responseIds) {
            ProductResponse response = productService.getProductResponseById(id);
            d = d + response.price() * response.quantity();
        }
        return d;
    }
}