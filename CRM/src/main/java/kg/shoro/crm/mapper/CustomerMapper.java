package kg.shoro.crm.mapper;

import kg.shoro.crm.model.Customer;
import kg.spring.shared.dto.response.CustomerResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
    List<CustomerResponse> toResponseList(List<Customer> customers);
}
