package com.pm.clothingshop.service;

import com.pm.clothingshop.dto.request.AddCartRequest;
import com.pm.clothingshop.dto.request.UpdateCartItemRequest;
import com.pm.clothingshop.dto.response.CartItemResponse;
import com.pm.clothingshop.dto.response.CartResponse;
import com.pm.clothingshop.exception.ResourceNotFoundException;
import com.pm.clothingshop.exception.UnauthorizedException;
import com.pm.clothingshop.model.Cart;
import com.pm.clothingshop.model.CartItem;
import com.pm.clothingshop.model.Product;
import com.pm.clothingshop.repository.CartItemRepository;
import com.pm.clothingshop.repository.CartRepository;
import com.pm.clothingshop.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        return convertToCartResponse(cart);
    }
    @Transactional
    public CartResponse addToCart(Long userId, AddCartRequest addCartRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        Product product = productRepository.findById(addCartRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + addCartRequest.getQuantity());
            cartItem.setSubTotal(cartItem.getPricePerItem() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(addCartRequest.getQuantity());
            cartItem.setSelectedSize(addCartRequest.getSelectedSize());
            cartItem.setPricePerItem(product.getPrice());
            cartItem.setSubTotal(product.getPrice() *  addCartRequest.getQuantity());
            cartItemRepository.save(cartItem);
        }
        updateCartTotal(cart);

        return convertToCartResponse(cart);
    }
    @Transactional
    public CartResponse updateCartItem(Long userId,
                                       Long cartItemId,
                                       UpdateCartItemRequest updateCartItemRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("You are not authorized to modify this cart item");
        }

        cartItem.setQuantity(updateCartItemRequest.getQuantity());
        cartItem.setSubTotal(cartItem.getPricePerItem() * updateCartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);

        updateCartTotal(cart);

        return convertToCartResponse(cart);
    }
    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this cart item");
        }

        cartItemRepository.delete(cartItem);

        updateCartTotal(cart);
        return convertToCartResponse(cart);
    }
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }
    public void updateCartTotal(Cart cart) {
        Double total = cart.getItems().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }
    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(cart.getId());
        cartResponse.setTotalPrice(cart.getTotalPrice());

        List<CartItemResponse> cartItemResponses = cart.getItems().stream()
                .map(this::convertToCartItemResponse)
                .toList();
        cartResponse.setItems(cartItemResponses);
        cartResponse.setTotalItems(cartItemResponses.size());

        return cartResponse;
    }
    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setProductId(cartItem.getProduct().getId());
        cartItemResponse.setProductName(cartItem.getProduct().getName());
        cartItemResponse.setProductImage(cartItem.getProduct().getImageUrl());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setSelectedSize(cartItem.getSelectedSize());
        cartItemResponse.setPricePerItem(cartItem.getPricePerItem());
        cartItemResponse.setSubTotal(cartItem.getSubTotal());
        return cartItemResponse;
    }
}
