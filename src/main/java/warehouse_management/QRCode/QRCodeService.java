package warehouse_management.QRCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeService {

    @Value("${app.qr.base-url}")
    private String baseUrl;

    public byte[] generateQRCode(Long productId, String name, String description) throws WriterException, IOException {
        String url = baseUrl + "/api/products/" + productId;

        // Set the desired square dimensions
        int imageSize = 600;

        // Generate QR Code (make it slightly smaller than the image to leave some padding)
        int qrSize = 400; // QR code will be 400x400
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, qrSize, qrSize);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Create the square canvas
        BufferedImage finalImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = finalImage.createGraphics();

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageSize, imageSize);

        // Draw QR code in the center-top part of the new square image
        int qrX = (imageSize - qrSize) / 2;
        int qrY = 30; // Small padding from the top
        g.drawImage(qrImage, qrX, qrY, null);

        // Text below QR code
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 20)); // Adjust font size for smaller image

        FontMetrics fm = g.getFontMetrics();
        int textYStart = qrY + qrSize + 20; // Padding below QR code

        String idLine = "ID: " + productId;
        String nameLine = "Ime: " + name;
        String descLine = "Opis: " + description;

        // Center the text
        g.drawString(idLine, centerText(idLine, fm, imageSize), textYStart);
        g.drawString(nameLine, centerText(nameLine, fm, imageSize), textYStart + fm.getHeight() + 5);
        g.drawString(descLine, centerText(descLine, fm, imageSize), textYStart + 2 * (fm.getHeight() + 5));

        g.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    // You'll need this helper method for centering text if you don't have it already
    private int centerText(String text, FontMetrics fm, int width) {
        int textWidth = fm.stringWidth(text);
        return (width - textWidth) / 2;
    }

    public String getProductUrl(Long productId) {
        return baseUrl + "/api/products/" + productId;
    }
}