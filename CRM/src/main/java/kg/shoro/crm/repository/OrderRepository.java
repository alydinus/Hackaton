package kg.shoro.crm.repository;

import kg.shoro.crm.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(
            value = """
            SELECT COUNT(*) 
            FROM orders o 
            WHERE o.created_at >= CURRENT_DATE - (:days || ' days')::interval
            """,
            nativeQuery = true
    )
    Long countOrdersInLastDays(@Param("days") Long days);


    @Query("SELECT o from  Order o JOIN FETCH o.products")
    List<Order> findAllWithProducts();
}
