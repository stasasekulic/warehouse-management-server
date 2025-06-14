package Product;

import Audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditService auditService;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product, Long userId) {
        Product savedProduct = productRepository.save(product);
        auditService.logProductCreation(savedProduct, userId);
        return savedProduct;
    }

    public Product updateProduct(Long id, Product updatedProduct, Long userId) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();

            // Create a copy of the existing product for audit purposes
            Product oldProduct = new Product();
            oldProduct.setId(existingProduct.getId());
            oldProduct.setName(existingProduct.getName());
            oldProduct.setDescription(existingProduct.getDescription());
            oldProduct.setPrice(existingProduct.getPrice());
            oldProduct.setStockQuantity(existingProduct.getStockQuantity());

            // Update the existing product
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStockQuantity(updatedProduct.getStockQuantity());

            Product savedProduct = productRepository.save(existingProduct);

            // Log the changes
            auditService.logProductUpdate(oldProduct, savedProduct, userId);

            return savedProduct;
        }
        throw new RuntimeException("Product not found with id: " + id);
    }

    public void deleteProduct(Long id, Long userId) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            auditService.logProductDeletion(product, userId);
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
