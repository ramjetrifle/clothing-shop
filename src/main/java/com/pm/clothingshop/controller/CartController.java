package com.pm.clothingshop.controller;

import com.pm.clothingshop.dto.request.AddCartRequest;
import com.pm.clothingshop.dto.request.UpdateCartItemRequest;
import com.pm.clothingshop.dto.response.CartResponse;
import com.pm.clothingshop.exception.BadRequestException;
import com.pm.clothingshop.exception.ResourceNotFoundException;
import com.pm.clothingshop.model.Cart;
import com.pm.clothingshop.repository.CartItemRepository;
import com.pm.clothingshop.repository.CartRepository;
import com.pm.clothingshop.service.CartService;
import com.pm.clothingshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    public CartController(CartService cartService,  UserService userService,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository) {
        this.cartService = cartService;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = userService.getCurrentUserId();
        CartResponse cartResponse = cartService.getCart(userId);
        return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddCartRequest addCartRequest) {
        Long userId = userService.getCurrentUserId();
        CartResponse cartResponse = cartService.addToCart(userId, addCartRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long cartItemId,
                                                       @Valid @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        Long userId = userService.getCurrentUserId();
        CartResponse cartResponse = cartService.updateCartItem(userId, cartItemId, updateCartItemRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/pay")
    @Transactional
    public ResponseEntity<Map<String, String>> payCart() {
        Long userId = userService.getCurrentUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot pay for empty cart");
        }

        Double totalPaid = cart.getTotalPrice();

        cart.getItems().clear();

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Payment successful!");
        response.put("amount", String.format("$%.2f", totalPaid));

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long cartItemId) {
        Long userId = userService.getCurrentUserId();
        CartResponse cartResponse = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(cartResponse);
    }
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId = userService.getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
