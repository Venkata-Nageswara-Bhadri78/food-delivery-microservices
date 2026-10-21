package com.natixis.customer.common.service.impl;

import com.natixis.customer.common.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.natixis.customer.common.dto.CreateCustomerRequest;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.exception.CustomerAlreadyExistsException;
import com.natixis.customer.common.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;

    public void createCustomer(@RequestBody CreateCustomerRequest request){

        if(customerRepository.existsByUserId(request.getUserId())){
            throw new CustomerAlreadyExistsException("Customer Already Exists");
        }
        Customer customer = Customer.builder()
                                .email(request.getEmail())
                                .userId(request.getUserId())
                                .phone(request.getPhone())
                                .address(request.getAddress())
                                .build();
        customerRepository.save(customer);
    }

}
