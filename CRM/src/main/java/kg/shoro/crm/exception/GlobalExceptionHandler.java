package kg.shoro.crm.exception;

import kg.spring.shared.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ErrorResponse handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                404,
                "NOT_FOUND",
                java.time.Instant.now()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ErrorResponse handleOrderNotFoundException(OrderNotFoundException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                404,
                "NOT_FOUND",
                java.time.Instant.now()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorResponse handleProductNotFoundException(ProductNotFoundException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                404,
                "NOT_FOUND",
                java.time.Instant.now()
        );
    }
}
