package com.natixis.customer.common.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.natixis.customer.common.dto.CreateCustomerRequest;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.exception.CustomerAlreadyExistsException;
import com.natixis.customer.common.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void createCustomerSavesProfile() {
        when(customerRepository.existsByUserId(3L)).thenReturn(false);

        customerService.createCustomer(request());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertEquals("ada@test.com", captor.getValue().getEmail());
        assertEquals(3L, captor.getValue().getUserId());
        assertEquals("12 Main Street", captor.getValue().getAddress());
    }

    @Test
    void createCustomerRejectsExistingUser() {
        when(customerRepository.existsByUserId(3L)).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(request()));
        verify(customerRepository, never()).save(any());
    }

    private static CreateCustomerRequest request() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("ada@test.com");
        request.setUserId(3L);
        request.setPhone("9999999999");
        request.setAddress("12 Main Street");
        return request;
    }
}
