package kg.shoro.payment_service.controller.api;

import kg.shoro.payment_service.dto.PartDto;
import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionCreateRequest request) {
        return new ResponseEntity<>(
                paymentService.createSession(request),
                HttpStatus.OK);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        return new ResponseEntity<>(
                paymentService.getSession(sessionId),
                HttpStatus.OK);
    }

    @GetMapping("/pay/{linkId}")
    public ResponseEntity<PartDto> getPaymentPart(@PathVariable String linkId) {
        return new ResponseEntity<>(
                paymentService.getPartByLinkId(linkId),
                HttpStatus.OK);
    }
    
    @PostMapping("/pay/{linkId}")
    public ResponseEntity<PartDto> payPart(@PathVariable String linkId) {
        return new ResponseEntity<>(
                paymentService.payPart(linkId),
                HttpStatus.OK);
    }
}

