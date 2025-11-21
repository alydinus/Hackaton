package kg.shoro.crm.repository;

import kg.shoro.crm.model.OrderProduct;
import kg.shoro.crm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT op FROM OrderProduct op WHERE op.id IN :longs")
    List<OrderProduct> findAllByIdIn(List<Long> longs);

}
