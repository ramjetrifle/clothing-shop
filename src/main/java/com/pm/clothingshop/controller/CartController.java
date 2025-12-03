package com.pm.clothingshop.controller;

import com.pm.clothingshop.dto.request.AddCartRequest;
import com.pm.clothingshop.dto.request.UpdateCartItemRequest;
import com.pm.clothingshop.dto.response.CartResponse;
import com.pm.clothingshop.service.CartService;
import com.pm.clothingshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    public CartController(CartService cartService,  UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
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
