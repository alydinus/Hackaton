package kg.shoro.crm.service.impl;

import kg.spring.shared.dto.request.CreateCustomerRequest;
import kg.spring.shared.dto.request.UpdateCustomerRequest;
import kg.shoro.crm.exception.CustomerNotFoundException;
import kg.shoro.crm.model.Customer;
import kg.shoro.crm.repository.CustomerRepository;
import kg.shoro.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id " + id)
        );
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer createCustomer(CreateCustomerRequest request) {
        return customerRepository.save(Customer.builder()
                .name(request.name())
                .debt(request.debt())
                .build());
    }

    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id " + id)
        );
        customer.setName(request.name());
        customer.setDebt(request.debt());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id " + id)
        );
        customerRepository.delete(customer);
    }
}
