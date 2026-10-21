package com.natixis.customer.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.natixis.customer.cart.exception.CartItemAlreadyExistsException;
import com.natixis.customer.cart.exception.CartItemNotFoundException;
import com.natixis.customer.cart.exception.InvalidCartItemException;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.coupon.exception.CouponAlreadyUsedException;
import com.natixis.customer.coupon.exception.CouponNotFoundException;
import com.natixis.customer.order.exception.AgentNotFoundException;
import com.natixis.customer.order.exception.OrdersNotFoundException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler (CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomerNotFound(CustomerNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler (EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyExists(EmailAlreadyExistsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (CartItemNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleCartItemNotFound(CartItemNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (CartItemAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleCartItemAlreadyExists(CartItemAlreadyExistsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler (OrdersNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleOrderNotFound(OrdersNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (CouponNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleCouponNotFound(CouponNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler (CouponAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<String>> handleCouponAlreadyUsed(CouponAlreadyUsedException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (InvalidCartItemException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCartItem(InvalidCartItemException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (CustomerAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomerAlreadyExists(CustomerAlreadyExistsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (AgentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAgentNotFound(AgentNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
