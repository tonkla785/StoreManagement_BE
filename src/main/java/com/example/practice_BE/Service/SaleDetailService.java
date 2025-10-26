package com.example.practice_BE.Service;

import com.example.practice_BE.Entity.ProductEntity;
import com.example.practice_BE.Repository.SaleDetailRepository;
import com.example.practice_BE.Repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SaleRepository saleRepository;

    @Autowired
    public SaleDetailService(SaleDetailRepository saleDetailRepository,
                             SaleRepository saleRepository) {
        this.saleDetailRepository = saleDetailRepository;
        this.saleRepository = saleRepository;
    }

    //Get Total sale product
    public List<Map<String, Object>> getTotalQuantityForAllProducts() {
        List<Object[]> results = saleDetailRepository.getTotalQuantityForAllProducts();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            ProductEntity product = (ProductEntity) row[0];
            Long totalQuantity = (Long) row[1];

            Map<String, Object> map = new HashMap<>();
            map.put("productId", product.getProductId());
            map.put("productName", product.getProductName());
            map.put("totalQuantity", totalQuantity);

            data.add(map);
        }

        return data;
    }

    //Get total sale summary Month
    public double getTotalSalesLast30Days() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            Timestamp startDate = Timestamp.valueOf(thirtyDaysAgo);

            Double total = saleRepository.getTotalSalesLast30Days(startDate);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating last 30 days sales total", e);
        }
    }

    //Get total sale by select
    public double getTotalSalesBetween(LocalDateTime start, LocalDateTime end) {
        try {
            Double total = saleRepository.getTotalSalesBetween(start, end);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating sales total between range", e);
        }
    }
}
