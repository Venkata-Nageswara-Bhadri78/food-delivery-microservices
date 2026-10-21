package com.natixis.customer.common.service;

import com.natixis.customer.common.dto.CreateCustomerRequest;

public interface CustomerService {
    public void createCustomer(CreateCustomerRequest createCustomerRequest);
}
