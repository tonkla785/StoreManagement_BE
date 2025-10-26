package com.example.practice_BE.Controllers;

import com.example.practice_BE.Service.SaleDetailService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class SaleDetailController {

    private final SaleDetailService saleDetailService;

    public SaleDetailController(SaleDetailService saleDetailService){
        this.saleDetailService = saleDetailService;
    }

    @GetMapping("/sum-all-products")
    public ResponseEntity<?> getAllProductSales() {
        try {
            List<Map<String, Object>> data = saleDetailService.getTotalQuantityForAllProducts();
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลสำเร็จ",
                    "data", data
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/monthly-sale")
    public ResponseEntity<?> getMonthlySale() {
        try {
            double summary = saleDetailService.getTotalSalesLast30Days();
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลสำเร็จ",
                    "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/sale-range")
    public ResponseEntity<?> getSaleBetween(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        try {
            LocalDateTime startDateTime = start.atStartOfDay(); // 00:00:00
            LocalDateTime endDateTime = end.atTime(LocalTime.MAX); // 23:59:59

            double total = saleDetailService.getTotalSalesBetween(startDateTime, endDateTime);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลสำเร็จ",
                    "data", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }
}
