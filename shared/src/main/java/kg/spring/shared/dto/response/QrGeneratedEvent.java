package kg.spring.shared.dto.response;

public record QrGeneratedEvent(
        Long orderId,
        String filePath
) {
}
