package com.running.you_run.user.controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {

    @PostMapping("/scan")
    public ResponseEntity<?> scanQRCode(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result;

            try {
                result = new MultiFormatReader().decode(bitmap);
            } catch (NotFoundException e) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("QR 코드를 인식하지 못했습니다.");
            }

            String qrText = result.getText();
            return ResponseEntity.ok(qrText);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("QR 처리 중 서버 오류가 발생했습니다.");
        }
    }
}
