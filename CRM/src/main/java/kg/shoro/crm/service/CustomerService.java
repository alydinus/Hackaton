package kg.shoro.crm.service;

import kg.shoro.crm.dto.request.CreateCustomerRequest;
import kg.shoro.crm.dto.request.UpdateCustomerRequest;
import kg.shoro.crm.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer getCustomerById(Long id);
    List<Customer> getAllCustomers();
    Customer createCustomer(CreateCustomerRequest request);
    Customer updateCustomer(Long id, UpdateCustomerRequest request);
    void deleteCustomer(Long id);
}
