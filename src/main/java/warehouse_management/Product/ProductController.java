package warehouse_management.Product;

import warehouse_management.Audit.AuditService;
import warehouse_management.ProductAuditLog.ProductAuditLog;
import warehouse_management.QRCode.QRCodeService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product,
                                                 @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {
        try {
            Product createdProduct = productService.createProduct(product, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestBody Product product,
                                                 @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {
        try {
            Product updatedProduct = productService.updateProduct(id, product, userId);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id,
                                              @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {
        try {
            productService.deleteProduct(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProducts(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long id) {
        try {
            // Check if product exists
            Optional<Product> product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            byte[] qrCode = qrCodeService.generateQRCode(product.get().getId(), product.get().getName(), product.get().getDescription());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCode.length);
            headers.set("Content-Disposition", "inline; filename=product-" + id + "-qr.png");

            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);

        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/qr-url")
    public ResponseEntity<String> getQRCodeUrl(@PathVariable Long id) {
        // Check if product exists
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String url = qrCodeService.getProductUrl(id);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<ProductAuditLog>> getProductAuditHistory(@PathVariable Long id) {
        // Check if product exists
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductAuditLog> auditHistory = auditService.getProductAuditHistory(id);
        return ResponseEntity.ok(auditHistory);
    }
}
