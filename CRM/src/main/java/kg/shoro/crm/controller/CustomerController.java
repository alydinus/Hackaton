package kg.shoro.crm.controller;

import jakarta.validation.Valid;
import kg.shoro.crm.dto.request.CreateCustomerRequest;
import kg.shoro.crm.dto.request.UpdateCustomerRequest;
import kg.shoro.crm.mapper.CustomerMapper;
import kg.shoro.crm.service.CustomerService;
import kg.spring.shared.dto.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return new ResponseEntity<>(
                customerMapper.toResponseList(customerService.getAllCustomers()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return new ResponseEntity<>(
                customerMapper.toResponse(customerService.getCustomerById(id)),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CreateCustomerRequest request) {
        return new ResponseEntity<>(
                customerMapper.toResponse(customerService.createCustomer(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCustomerRequest request
    ) {
        return new ResponseEntity<>(
                customerMapper.toResponse(customerService.updateCustomer(id, request)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
