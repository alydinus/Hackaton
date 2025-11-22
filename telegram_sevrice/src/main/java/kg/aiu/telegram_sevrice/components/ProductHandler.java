package kg.aiu.telegram_sevrice.components;

import kg.aiu.telegram_sevrice.components.rabbit.RabbitRpcClient;
import kg.aiu.telegram_sevrice.components.rabbit.RabbitSender;
import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.DeleteProductRequest;
import kg.spring.shared.dto.response.ProductResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class ProductHandler {

    private final RabbitRpcClient rabbitClient;
    private final RabbitSender rabbitSender;
    private final @Lazy TelegramBot bot;
    private final Random random;

    public ProductHandler(RabbitRpcClient rabbitClient, RabbitSender rabbitSender,@Lazy TelegramBot bot) {
        this.rabbitClient = rabbitClient;
        this.rabbitSender = rabbitSender;
        this.bot = bot;
        this.random = new Random();
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
            List<ProductResponse> products = rabbitClient.getAllProducts();

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
                        product.id(), product.name(), product.price(), product.quantity()
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
            ProductResponse product = rabbitClient.getProductById(productId);

            String message = String.format(
                    "📄 *Детали товара:*\n\n" +
                            "🆔 *ID:* %d\n" +
                            "📝 *Название:* %s\n" +
                            "📋 *Описание:* %s\n" +
                            "💵 *Цена:* %s ₽\n" +
                            "📦 *Количество:* %d шт.\n" +
//                            "📁 *Категория:* %s\n" +
                    product.id(),
                    product.name(),
                    product.description() != null ? product.description() : "Нет описания",
                    product.price(),
                    product.quantity()
//                    product.getCategory() != null ? product.getCategory() : "Не указана",
//                    null != null ? product.getCategory() : "Не указана"
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
                    Double price = Double.valueOf(text);
                    context.put("price", price);
                    session.setState(TelSessionModel.BotState.AWAITING_PRODUCT_STOCK);
                    bot.sendTextMessage(chatId, "📦 Введите количество:");
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


                    keyboard.setKeyboard(rows);


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

            CreateProductRequest product = new CreateProductRequest(

             random.nextLong() * System.currentTimeMillis(),
            (String) context.get("name"),
            (String) context.get("description"),
            Double.valueOf((String)context.get("price")),
            Integer.valueOf((String)context.get("stock"))
//            product.setCategory((String) context.get("category"));
            );

            rabbitSender.sendProduct(product);
            ProductResponse response = rabbitClient.getProductById(product.tempId());

            String message = String.format(
                    "✅ *Товар успешно создан!*\n\n" +
                            "🆔 ID: %d\n" +
                            "📝 Название: %s\n" +
                            "💵 Цена: %s ₽\n" +
                            "📦 Остаток: %d шт.\n" +
//                            "📁 Категория: %s",
                    response.id(), response.name(), response.price(),
                    response.quantity()
//                    , response.getCategory()
            );

            bot.sendTextMessage(chatId, message);

            session.reset();

        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при создании товара: " + e.getMessage());
            session.reset();
        }
    }

    public void startProductEditing(Long chatId, Long productId) {
        bot.sendTextMessage(chatId, "✏️ Редактирование товара ID: " + productId + "\n\nЭта функция в разработке...");
    }

    public void deleteProduct(Long chatId, Long productId) {
        try {
            rabbitSender.deleteProduct(new DeleteProductRequest(productId));
            bot.sendTextMessage(chatId, "✅ Товар успешно удален");
        } catch (Exception e) {
            bot.sendTextMessage(chatId, "❌ Ошибка при удалении товара: " + e.getMessage());
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
