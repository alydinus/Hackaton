# Hackaton 2025 Workspace

Много-модульный монорепозиторий (Maven parent POM) для демонстрации обмена событиями между сервисами через RabbitMQ.

## Модули
- crm – управление заказами, клиентами и товарами; публикация события OrderCreatedEvent, получение QrGeneratedEvent.
- qr-service – генерация QR-кодов по событиям создания заказа; публикация QrGeneratedEvent.
- shared – общие DTO и enum'ы (например OrderCreatedEvent, QrGeneratedEvent). Собирается в shared-1.0.jar.
- telegram_sevrice – Telegram бот (использует RabbitMQ, может расширяться для уведомлений).

## Технологии
- Java / Spring Boot
- Spring Data JPA (PostgreSQL)
- Spring AMQP (RabbitMQ)
- Lombok, Validation
- Docker / docker-compose (PostgreSQL, RabbitMQ)

## Архитектура событий
TopicExchange: `crm.exchange`

Очереди и routing key'и (имена совпадают с ключами):
- `order.created` – сообщение о создании заказа (OrderCreatedEvent). Публикует CRM, слушает qr-service.
- `qr.generated` – сообщение о готовом QR (QrGeneratedEvent). Публикует qr-service, слушает CRM.

Последовательность:
1. Клиент создает заказ через CRM (POST /api/v1/orders). CRM сохраняет в БД и публикует OrderCreatedEvent.
2. qr-service слушает `order.created`, генерирует QR, сохраняет файл в `./qr/` (см. настройку `qr.out-dir`), публикует QrGeneratedEvent в `qr.generated`.
3. CRM слушает `qr.generated` и прикрепляет путь к файлу QR к заказу.

## Конфигурация RabbitMQ
application.yaml / application.yml (в сервисах) содержат:
```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```
CRM дополнительно:
```
crm.exchange=crm.exchange
crm.routing.order-created=order.created
```
qr-service:
```
exchange=crm.exchange
queues.order-created=order.created
queues.qr-generated=qr.generated
```
Советы по предотвращению конфликтов:
- Используйте единый набор имен в shared (вынести в константы) чтобы избежать расхождений.
- Следите чтобы каждое Queue создавалось один раз (либо централизованно в CRM, либо в обоих сервисах c одинаковыми параметрами durable=true).
- При изменении routing key обновляйте значения одновременно в обоих сервисах.

## REST API (основные)
Базовый префикс CRM (предположительно): `/api/v1`

Customers:
- GET /api/v1/customers – список
- GET /api/v1/customers/{id}
- POST /api/v1/customers
- PUT /api/v1/customers/{id}
- DELETE /api/v1/customers/{id}

Products:
- GET /api/v1/products
- GET /api/v1/products/{id}
- POST /api/v1/products
- PUT /api/v1/products/{id}
- DELETE /api/v1/products/{id}

Orders:
- GET /api/v1/orders
- GET /api/v1/orders/{id}
- POST /api/v1/orders (триггер публикации OrderCreatedEvent)
- PUT /api/v1/orders/{id}
- DELETE /api/v1/orders/{id}

QR-Service:
- GET /api/v1/qr/{orderId} – получить информацию / статус (по содержимому QrController).

(Проверьте фактический базовый @RequestMapping в контроллерах; при необходимости обновите префикс.)

## Сборка
Корень содержит Parent POM.
```
mvn clean install -DskipTests
```
Соберутся артефакты модулей, shared будет установлен в локальный репозиторий.

## Запуск инфраструктуры
```
docker compose up -d
```
Поднимутся:
- PostgreSQL (порт 5435 -> 5432 контейнера, БД crm_db)
- RabbitMQ (порт 5672 AMQP, 15672 UI http://localhost:15672 guest/guest)

## Запуск сервисов
(В отдельных терминалах из папок модулей)
```
cd CRM
mvn spring-boot:run

cd qr-service
mvn spring-boot:run

cd telegram_sevrice
mvn spring-boot:run
```
Порты (по конфигам):
- CRM: 8080 (если не изменено; проверьте application.yaml)
- qr-service: 8081
- telegram_sevrice: 8086

## Тестирование потока событий
1. Создайте заказ: `POST http://localhost:8080/api/v1/orders` (тело согласно DTO CreateOrderRequest).
2. Убедитесь что сообщение появилось в очереди `order.created` (RabbitMQ UI).
3. После генерации QR появится сообщение в `qr.generated`.
4. Проверьте заказ (GET /api/v1/orders/{id}) – поле с путем к QR должно быть заполнено.

## Директория QR файлов
`qr-service` сохраняет QR в `./qr/` (относительно корня репо) – изображения вида `order_<id>.png`.
Добавьте .gitignore при необходимости, если файлы не должны коммититься.

## Telegram Bot
Конфигурация токена сейчас в репозитории (application.yml). Рекомендуется вынести:
```
telegram.bot.token=${TELEGRAM_BOT_TOKEN}
```
и задавать переменную окружения, чтобы не хранить секреты в git.

## Расширения / TODO
- Вынести имена exchange/queues/routing key в модуль shared (класс Constants).
- Добавить описание payload DTO в README (структура OrderCreatedEvent / QrGeneratedEvent).
- Добавить OpenAPI/Swagger для REST.
- Покрыть интеграционными тестами цепочку публикации/обработки.

## Troubleshooting
- Queue не создается: убедитесь что @EnableRabbit и конфиг RabbitConfig загружен.
- Сообщения сериализуются неверно: проверьте Jackson2JsonMessageConverter в обоих сервисах.
- 404 эндпоинты: проверьте базовый префикс @RequestMapping в контроллерах.
- Ошибка подключения к БД: проверьте docker compose и порт 5435.

## Лицензия
См. файл LICENSE.
