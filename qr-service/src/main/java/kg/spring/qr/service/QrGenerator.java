package kg.spring.qr.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class QrGenerator {

    @Value("${qr.out-dir}")
    private String qrDir;

    private static final int SIZE = 400;

    public String generateQrFile(Long orderId, String content) {
        try {
            File dir = new File(qrDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = qrDir + "/order_" + orderId + ".png";

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    SIZE,
                    SIZE,
                    hints
            );

            MatrixToImageWriter.writeToPath(matrix, "PNG", Path.of(filePath));

            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("QR generation failed: " + e.getMessage());
        }
    }
}
