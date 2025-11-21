package kg.shoro.crm.repository;

import kg.shoro.crm.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query("SELECT op FROM OrderProduct op JOIN FETCH op.product WHERE op.id IN :productIds")
    List<OrderProduct> findAllByIdWithProducts(List<Long> productIds);
}
