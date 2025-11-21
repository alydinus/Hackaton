package kg.shoro.crm.service;

import kg.shoro.crm.model.OrderProduct;

import java.util.List;

public interface OrderProductService {
    List<OrderProduct> getOrderProductsByOrderId(List<Long> orderIds);
}
