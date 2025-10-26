package com.example.practice_BE.Repository;

import com.example.practice_BE.Entity.SaleDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository <SaleDetailEntity, Long>{

    @Query("SELECT d.productId, SUM(d.quantitySale) FROM SaleDetailEntity d WHERE d.saleId IS NOT NULL GROUP BY d.productId")
    List<Object[]> getTotalQuantityForAllProducts();

}
