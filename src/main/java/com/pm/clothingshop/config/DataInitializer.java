package com.pm.clothingshop.config;

import com.pm.clothingshop.enums.Role;
import com.pm.clothingshop.model.Cart;
import com.pm.clothingshop.model.Category;
import com.pm.clothingshop.model.Product;
import com.pm.clothingshop.model.User;
import com.pm.clothingshop.repository.CategoryRepository;
import com.pm.clothingshop.repository.ProductRepository;
import com.pm.clothingshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Category> categories = createCategories();
            int productCount = createProducts(categories);
            createAdminUser();
        } else {
            System.out.println("Database already exists");
        }
    }
    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(createCategory("T-Shirts", "Comfortable t-shirts for everyday wear"));
        categories.add(createCategory("Shirts", "Dress shirts, casual shirts, and button-ups"));
        categories.add(createCategory("Pants", "Jeans, chinos, and dress pants"));
        categories.add(createCategory("Hoodies", "Cozy hoodies and sweatshirts"));
        categories.add(createCategory("Jackets", "Outerwear for all seasons"));
        categories.add(createCategory("Shoes", "Sneakers, boots, and casual footwear"));
        categories.add(createCategory("Accessories", "Hats, belts, bags, and more"));
        return categoryRepository.saveAll(categories);
    }
    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
    private int createProducts(List<Category> categories) {
        List<Product> allProducts = new ArrayList<>();
        Category tshirts = categories.get(0);
        Category shirts = categories.get(1);
        Category pants = categories.get(2);
        Category hoodies = categories.get(3);
        Category jackets = categories.get(4);
        Category shoes = categories.get(5);
        Category accessories = categories.get(6);

        // T-Shirts
        allProducts.add(createProduct("Plain White T-Shirt", "Classic comfortable cotton t-shirt", 19.99,
                Arrays.asList("XS", "S", "M", "L", "XL", "XXL"), tshirts, "/images/tshirts/white-tshirt.jpg", 100L));
        allProducts.add(createProduct("Plain Black T-Shirt", "Essential black cotton t-shirt", 19.99,
                Arrays.asList("XS", "S", "M", "L", "XL", "XXL"), tshirts, "/images/tshirts/black-tshirt.jpg", 100L));

// Shirts
        allProducts.add(createProduct("White Dress Shirt", "Professional white dress shirt", 49.99,
                Arrays.asList("S", "M", "L", "XL", "XXL"), shirts, "/images/shirts/white-shirt.jpg", 60L));
        allProducts.add(createProduct("Denim Shirt", "Casual denim button-up", 42.99,
                Arrays.asList("S", "M", "L", "XL"), shirts, "/images/shirts/blue-denim-shirt.jpg", 40L));

// Pants
        allProducts.add(createProduct("Blue Jeans", "Classic blue denim jeans", 59.99,
                Arrays.asList("28", "30", "32", "34", "36", "38"), pants, "/images/pants/blue-denim-jeans.jpg", 80L));
        allProducts.add(createProduct("Black Jeans", "Sleek black denim", 59.99,
                Arrays.asList("28", "30", "32", "34", "36", "38"), pants, "/images/pants/black-jeans.jpg", 80L));

// Hoodies
        allProducts.add(createProduct("Black Hoodie", "Classic black pullover hoodie", 49.99,
                Arrays.asList("S", "M", "L", "XL", "XXL"), hoodies, "/images/hoodies/black-hoodie.jpg", 60L));
        allProducts.add(createProduct("Gray Hoodie", "Comfortable gray hoodie", 49.99,
                Arrays.asList("S", "M", "L", "XL", "XXL"), hoodies, "/images/hoodies/gray-hoodie.jpg", 60L));

// Jackets
        allProducts.add(createProduct("Denim Jacket", "Classic blue denim jacket", 79.99,
                Arrays.asList("S", "M", "L", "XL"), jackets, "/images/jackets/denim-jacket.jpg", 40L));

// Shoes
        allProducts.add(createProduct("White Sneakers", "Clean white sneakers", 79.99,
                Arrays.asList("7", "8", "9", "10", "11", "12"), shoes, "/images/shoes/white-sneakers.jpg", 50L));
        allProducts.add(createProduct("Black Sneakers", "Classic black sneakers", 79.99,
                Arrays.asList("7", "8", "9", "10", "11", "12"), shoes, "/images/shoes/black-sneakers.jpg", 50L));
        allProducts.add(createProduct("Running Shoes", "Performance running shoes", 99.99,
                Arrays.asList("7", "8", "9", "10", "11", "12"), shoes, "/images/shoes/running-shoes.jpg", 40L));

// Accessories
        allProducts.add(createProduct("Baseball Cap", "Classic adjustable cap", 24.99,
                Arrays.asList("One Size"), accessories, "/images/accessories/cap.jpg", 100L));
        allProducts.add(createProduct("Beanie", "Warm knit beanie", 19.99,
                Arrays.asList("One Size"), accessories, "/images/accessories/beanie.jpg", 80L));
        allProducts.add(createProduct("Black Belt", "Leather dress belt", 29.99,
                Arrays.asList("S", "M", "L", "XL"), accessories, "/images/accessories/black-belt.jpg", 60L));
        allProducts.add(createProduct("Sunglasses", "UV protection sunglasses", 39.99,
                Arrays.asList("One Size"), accessories, "/images/accessories/black-sunglasses.jpg", 70L));
        productRepository.saveAll(allProducts);
        return allProducts.size();
    }
    private Product createProduct(String name, String description, double price,
                                  List<String> sizes, Category category,
                                  String imageUrl, long quantity) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setSize(sizes);
        product.setImageUrl(imageUrl);
        product.setQuantity(quantity);
        return product;
    }
    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@clothingshop.com")) {
            User admin =  new User();
            admin.setUsername("admin");
            admin.setEmail("admin@clothingshop.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);

            Cart cart = new Cart();
            cart.setUser(admin);
            cart.setTotalPrice(0.0);
            admin.setCart(cart);
            userRepository.save(admin);
        }
    }
}
