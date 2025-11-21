package kg.shoro.payment_service.controller.api;

import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.mapper.PaymentSessionMapper;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSessionMapper paymentSessionMapper;

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionCreateRequest request) {
        return new ResponseEntity<>(
                paymentSessionMapper.toResponse(paymentService.createSession(request)),
                HttpStatus.OK);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        return new ResponseEntity<>(
                paymentSessionMapper.toResponse(paymentService.getSession(sessionId)),
                HttpStatus.OK);
    }

//    @GetMapping("/sessions/{sessionId}/qr")
//    public ResponseEntity<byte[]> getQrForSession(@PathVariable Long sessionId) {
//        return ResponseEntity.ok(paymentService.getQrForSession(sessionId));
//    }

}

