package kg.shoro.crm.service.impl;

import kg.shoro.crm.model.OrderProduct;
import kg.shoro.crm.repository.OrderProductRepository;
import kg.shoro.crm.service.OrderProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductServiceImpl implements OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public List<OrderProduct> getOrderProductsByOrderId(List<Long> orderIds) {
        return orderProductRepository.findAllByIdWithProducts(orderIds);
    }
}
