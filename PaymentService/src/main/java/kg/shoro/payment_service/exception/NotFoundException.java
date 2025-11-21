package kg.shoro.payment_service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String productNotFound) {
        super(productNotFound);
    }
}