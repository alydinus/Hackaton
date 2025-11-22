package kg.aiu.telegram_sevrice.components;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TelSessionModel {

    private BotState state = BotState.IDLE;
    private String currentFlow;
    private Map<String, Object> context = new HashMap<>();
    private LocalDateTime lastUpdated = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BotState {
        IDLE,

        AWAITING_ORDER_CUSTOMER,
        AWAITING_ORDER_PRODUCT_SELECTION,
        AWAITING_ORDER_QUANTITY,
        AWAITING_ORDER_CONFIRMATION,
//        AWAITING_ORDER_PRODUCT_NAME,
//        AWAITING_ORDER_AMOUNT,

        AWAITING_PRODUCT_NAME,
        AWAITING_PRODUCT_DESCRIPTION,
        AWAITING_PRODUCT_PRICE,
        AWAITING_PRODUCT_STOCK,
        AWAITING_PRODUCT_CATEGORY,

        EDITING_ORDER,
        EDITING_PRODUCT_NAME,
        EDITING_PRODUCT_PRICE,
        EDITING_PRODUCT_STOCK,

        SEARCHING_ORDERS,
        SEARCHING_PRODUCTS,

        AWAITING_CONFIRMATION
    }

    public TelSessionModel() {}

    public TelSessionModel(BotState state, String currentFlow) {
        this.state = state;
        this.currentFlow = currentFlow;
    }

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(String currentFlow) {
        this.currentFlow = currentFlow;
        this.lastUpdated = LocalDateTime.now();
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void putToContext(String key, Object value) {
        this.context.put(key, value);
        this.lastUpdated = LocalDateTime.now();
    }

    public Object getFromContext(String key) {
        return this.context.get(key);
    }

    public String getStringFromContext(String key) {
        Object value = this.context.get(key);
        return value != null ? value.toString() : null;
    }

    public Long getLongFromContext(String key) {
        Object value = this.context.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Integer getIntegerFromContext(String key) {
        Object value = this.context.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public boolean isInState(BotState state) {
        return this.state == state;
    }

    public boolean isInFlow(String flow) {
        return flow != null && flow.equals(this.currentFlow);
    }

    public boolean isIdle() {
        return this.state == BotState.IDLE;
    }

    public boolean isCreatingProductResponse() {
        return isInFlow("PRODUCT_CREATION");
    }

    public boolean isCreatingOrderResponse() {
        return isInFlow("ORDER_CREATION");
    }

    public boolean isEditing() {
        return this.state.name().startsWith("EDITING_");
    }

    public boolean isSearching() {
        return this.state.name().startsWith("SEARCHING_");
    }

    public void startFlow(String flowName) {
        this.currentFlow = flowName;
        this.state = BotState.IDLE;
        this.context.clear();
        this.lastUpdated = LocalDateTime.now();
    }

    public void startProductResponseCreation() {
        startFlow("PRODUCT_CREATION");
        setState(BotState.AWAITING_PRODUCT_NAME);
    }

    public void startOrderResponseCreation() {
        startFlow("ORDER_CREATION");
        setState(BotState.AWAITING_ORDER_CUSTOMER);
    }

    public void startSearch(String searchType) {
        if ("PRODUCTS".equals(searchType)) {
            setState(BotState.SEARCHING_PRODUCTS);
        } else if ("ORDERS".equals(searchType)) {
            setState(BotState.SEARCHING_ORDERS);
        }
    }

    public void reset() {
        this.state = BotState.IDLE;
        this.currentFlow = null;
        this.context.clear();
        this.lastUpdated = LocalDateTime.now();
    }

    public void clearContext() {
        this.context.clear();
        this.lastUpdated = LocalDateTime.now();
    }

    public void updateLastActivity() {
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isExpired() {
        return lastUpdated.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    public boolean isExpired(long minutes) {
        return lastUpdated.plusMinutes(minutes).isBefore(LocalDateTime.now());
    }

    public long getMinutesSinceLastActivity() {
        return java.time.Duration.between(lastUpdated, LocalDateTime.now()).toMinutes();
    }

    public String getStateDescription() {
        switch (state) {
            case IDLE:
                return "Ожидание команд";
            case AWAITING_PRODUCT_NAME:
                return "Ожидание названия товара";
            case AWAITING_PRODUCT_DESCRIPTION:
                return "Ожидание описания товара";
            case AWAITING_PRODUCT_PRICE:
                return "Ожидание цены товара";
            case AWAITING_PRODUCT_STOCK:
                return "Ожидание количества товара";
            case AWAITING_PRODUCT_CATEGORY:
                return "Ожидание категории товара";
            case AWAITING_ORDER_CUSTOMER:
                return "Ожидание имени клиента";
            case AWAITING_ORDER_PRODUCT_SELECTION:
                return "Ожидание выбора товара";
            case AWAITING_ORDER_QUANTITY:
                return "Ожидание количества товара для заказа";
            case AWAITING_ORDER_CONFIRMATION:
                return "Ожидание подтверждения заказа";
            case SEARCHING_PRODUCTS:
                return "Поиск товаров";
            case SEARCHING_ORDERS:
                return "Поиск заказов";
            default:
                return state.name();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("state", state.name());
        map.put("currentFlow", currentFlow);
        map.put("context", new HashMap<>(context));
        map.put("lastUpdated", lastUpdated.toString());
        map.put("createdAt", createdAt.toString());
        return map;
    }

    public static TelSessionModel fromMap(Map<String, Object> map) {
        TelSessionModel session = new TelSessionModel();
        if (map.containsKey("state")) {
            session.setState(BotState.valueOf((String) map.get("state")));
        }
        if (map.containsKey("currentFlow")) {
            session.setCurrentFlow((String) map.get("currentFlow"));
        }
        if (map.containsKey("context")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> context = (Map<String, Object>) map.get("context");
            session.setContext(new HashMap<>(context));
        }
        return session;
    }

    @Override
    public String toString() {
        return "TelSessionModel{" +
                "state=" + state +
                ", currentFlow='" + currentFlow + '\'' +
                ", context=" + context +
                ", lastUpdated=" + lastUpdated +
                ", createdAt=" + createdAt +
                '}';
    }
}
