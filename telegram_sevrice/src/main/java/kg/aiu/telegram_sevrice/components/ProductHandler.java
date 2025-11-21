package kg.aiu.telegram_sevrice.components;

import kg.aiu.telegram_sevrice.service.OrderServiceClient;
import kg.spring.shared.dto.response.ProductResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductHandler {

    private final OrderServiceClient telService;
    private final TelegramBot bot;

    public ProductHandler(OrderServiceClient telService, TelegramBot bot) {
        this.telService = telService;
        this.bot = bot;
    }

    public void handleProductResponsesCommand(Long chatId) {
        String message = "📦 *Управление товарами*\n\nВыберите действие:";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("📥 Список товаров", "products_list_0"));
        row1.add(createButton("🔍 Поиск товара", "products_search"));
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("➕ Добавить товар", "product_create"));
        row2.add(createButton("📊 Остатки", "products_stock"));
        rows.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("📋 Главное меню", "main_menu"));
        rows.add(row3);

        keyboard.setKeyboard(rows);

        bot.sendMessageWithKeyboard(chatId, message, keyboard);
    }

    public void showProductResponsesList(Long chatId, int page) {
        try {
            List<ProductResponse> products = telService.getAllProductResponses();

            if (products.isEmpty()) {
                bot.sendTextMessage(chatId, "📭 Товары не найдены");
                return;
            }

            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) products.size() / pageSize);
            int start = page * pageSize;
            int end = Math.min(start + pageSize, products.size());

            StringBuilder message = new StringBuilder("📦 *Список товаров* (стр. " + (page + 1) + "/" + totalPages + "):\n\n");

            for (int i = start; i < end; i++) {
                ProductResponse product = products.get(i);
                message.append(String.format(
                        "🆔 *ID:* %d\n📝 *Название:* %s\n💵 *Цена:* %s ₽\n📦 *Остаток:* %d шт.\n\n",
                        product.getId(), product.getName(), product.getPrice(), product.getStockQuantity()
                ));
            }

            InlineKeyboardMarkup keyboard = createProductResponsesPaginationKeyboard(page, totalPages);
            bot.sendMessageWithKeyboard(chatId, message.toString(), keyboard);

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при получении товаров: " + e.getMessage());
        }
    }

    public void showProductResponseDetails(Long chatId, Long productId) {
        try {
            ProductResponse product = telService.getProductResponseById(productId);

            String message = String.format(
                    "📄 *Детали товара:*\n\n" +
                            "🆔 *ID:* %d\n" +
                            "📝 *Название:* %s\n" +
                            "📋 *Описание:* %s\n" +
                            "💵 *Цена:* %s ₽\n" +
                            "📦 *Остаток:* %d шт.\n" +
                            "📁 *Категория:* %s\n" +
                            "⏰ *Создан:* %s",
                    product.getId(),
                    product.getName(),
                    product.getDescription() != null ? product.getDescription() : "Нет описания",
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getCategory() != null ? product.getCategory() : "Не указана",
                    product.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            );

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            row1.add(createButton("✏️ Редактировать", "product_edit_" + productId));
            row1.add(createButton("🗑️ Удалить", "product_delete_" + productId));
            rows.add(row1);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            row2.add(createButton("📦 Использовать в заказе", "use_product_" + productId));
            row2.add(createButton("📋 Назад к списку", "products_list_0"));
            rows.add(row2);

            keyboard.setKeyboard(rows);

            bot.sendMessageWithKeyboard(chatId, message, keyboard);

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при получении товара: " + e.getMessage());
        }
    }

    public void startProductResponseCreation(Long chatId, TelSessionModel session) {
        session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_NAME);
        session.setCurrentFlow("PRODUCT_CREATION");
        session.getContext().clear();

        bot.sendTextMessage(chatId, "🏪 *Создание нового товара*\n\nВведите название товара:");
    }

    public void processProductResponseCreation(Long chatId, String text, TelSessionModel session) {
        Map<String, Object> context = session.getContext();

        switch (session.getState()) {
            case AWAITING_PRODUCT_NAME:
                context.put("name", text);
                session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_DESCRIPTION);
                bot.sendTextMessage(chatId, "📋 Введите описание товара:");
                break;

            case AWAITING_PRODUCT_DESCRIPTION:
                context.put("description", text);
                session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_PRICE);
                bot.sendTextMessage(chatId, "💵 Введите цену товара (только число):");
                break;

            case AWAITING_PRODUCT_PRICE:
                try {
                    BigDecimal price = new BigDecimal(text);
                    context.put("price", price);
                    session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_STOCK);
                    bot.sendTextMessage(chatId, "📦 Введите количество на складе:");
                } catch (NumberFormatException e) {
                    bot.sendTextMessage(chatId, "❌ Неверный формат цены. Введите число:");
                }
                break;

            case AWAITING_PRODUCT_STOCK:
                try {
                    Integer stock = Integer.parseInt(text);
                    context.put("stock", stock);
                    session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_CATEGORY);

                    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();

                    String[] categories = {"Электроника", "Одежда", "Книги", "Продукты", "Другое"};
                    for (String category : categories) {
                        List<InlineKeyboardButton> row = new ArrayList<>();
                        row.add(createButton(category, "product_category_" + category));
                        rows.add(row);
                    }

                    keyboard.setKeyboard(rows);

                    bot.sendMessageWithKeyboard(chatId, "📁 Выберите или введите категорию:", keyboard);

                } catch (NumberFormatException e) {
                    bot.sendTextMessage(chatId, "❌ Неверный формат количества. Введите число:");
                }
                break;

            case AWAITING_PRODUCT_CATEGORY:
                context.put("category", text);
                completeProductResponseCreation(chatId, session);
                break;
        }
    }

    public void completeProductResponseCreation(Long chatId, TelSessionModel session) {
        try {
            Map<String, Object> context = session.getContext();

            ProductResponse product = new ProductResponse();
            product.setName((String) context.get("name"));
            product.setDescription((String) context.get("description"));
            product.setPrice((BigDecimal) context.get("price"));
            product.setStockQuantity((Integer) context.get("stock"));
            product.setCategory((String) context.get("category"));
            product.setCreatedAt(java.time.LocalDateTime.now());

            ProductResponse createdProductResponse = telService.createProductResponse(product);

            String message = String.format(
                    "✅ *Товар успешно создан!*\n\n" +
                            "🆔 ID: %d\n" +
                            "📝 Название: %s\n" +
                            "💵 Цена: %s ₽\n" +
                            "📦 Остаток: %d шт.\n" +
                            "📁 Категория: %s",
                    createdProductResponse.getId(), createdProductResponse.getName(), createdProductResponse.getPrice(),
                    createdProductResponse.getStockQuantity(), createdProductResponse.getCategory()
            );

            bot.sendTextMessage(chatId, message);

            session.reset();

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при создании товара: " + e.getMessage());
            session.reset();
        }
    }

    public void startProductResponseEditing(Long chatId, Long productId) {
        // Реализация редактирования товара
        bot.sendTextMessage(chatId, "✏️ Редактирование товара ID: " + productId + "\n\nЭта функция в разработке...");
    }

    public void deleteProductResponse(Long chatId, Long productId) {
        try {
            telService.deleteProductResponse(productId);
            bot.sendTextMessage(chatId, "✅ Товар успешно удален");
        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при удалении товара: " + e.getMessage());
        }
    }

    public void searchProductResponses(Long chatId, String query) {
        try {
            List<ProductResponse> products = telService.searchProductResponses(query);

            if (products.isEmpty()) {
                bot.sendTextMessage(chatId, "🔍 Товары по запросу '" + query + "' не найдены");
                return;
            }

            StringBuilder message = new StringBuilder("🔍 *Результаты поиска:* '" + query + "'\n\n");

            for (ProductResponse product : products) {
                message.append(String.format(
                        "🆔 *ID:* %d\n📝 *Название:* %s\n💵 *Цена:* %s ₽\n📦 *Остаток:* %d шт.\n\n",
                        product.getId(), product.getName(), product.getPrice(), product.getStockQuantity()
                ));
            }

            bot.sendTextMessage(chatId, message.toString());

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при поиске товаров: " + e.getMessage());
        }
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private InlineKeyboardMarkup createProductResponsesPaginationKeyboard(int currentPage, int totalPages) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> navRow = new ArrayList<>();

        if (currentPage > 0) {
            navRow.add(createButton("⬅️ Назад", "products_list_" + (currentPage - 1)));
        }

        if (currentPage < totalPages - 1) {
            navRow.add(createButton("Вперед ➡️", "products_list_" + (currentPage + 1)));
        }

        if (!navRow.isEmpty()) {
            rows.add(navRow);
        }

        List<InlineKeyboardButton> refreshRow = new ArrayList<>();
        refreshRow.add(createButton("🔄 Обновить", "products_list_" + currentPage));
        rows.add(refreshRow);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(createButton("📦 Управление товарами", "products_list_0"));
        backRow.add(createButton("📋 Главное меню", "main_menu"));
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
