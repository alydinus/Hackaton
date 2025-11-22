package kg.spring.qr.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/qr")
public class QrController {

    @GetMapping("/{orderId}")
    public ResponseEntity<FileSystemResource> getQr(@PathVariable Long orderId) {

        File file = new File("./qr/order_" + orderId + ".png");

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new FileSystemResource(file));
    }
}