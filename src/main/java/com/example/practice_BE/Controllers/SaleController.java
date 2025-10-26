package com.example.practice_BE.Controllers;

import com.example.practice_BE.DTO.SaleDetailRequestDTO;
import com.example.practice_BE.Entity.SaleEntity;
import com.example.practice_BE.Service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSale(@RequestBody List<SaleDetailRequestDTO> saleDetails) {
        try {
            SaleEntity sale = saleService.createSale(saleDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "สร้างบิลสำเร็จ",
                    "data", sale
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 400,
                    "responseMessage", e.getMessage()
            ));
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 404,
                    "responseMessage", e.getMessage()
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        try {
            SaleEntity sale = saleService.getSaleById(id);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลบิลสำเร็จ",
                    "data", sale
            ));
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 404,
                    "responseMessage", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSales(@RequestParam("keyword") String keyword) {
        try {
            List<SaleEntity> Sale = saleService.searchSalesByName(keyword);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลบิลสำเร็จ",
                    "data", Sale
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllBill() {
        try {
            List<SaleEntity> Sale = saleService.findAll(Sort.by(Sort.Direction.ASC, "saleId"));
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลบิลทั้งหมดสำเร็จ",
                    "data", Sale
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id,@RequestBody List<SaleDetailRequestDTO> updatedDetails){
        try{
            SaleEntity sale = saleService.updateSale(id, updatedDetails);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "อัปเดตบิลสำเร็จ",
                    "data", sale
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 400,
                    "responseMessage", e.getMessage()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 404,
                    "responseMessage", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id){
        try{
            saleService.deleteSale(id);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ลบบิลสำเร็จ"
            ));
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 404,
                    "responseMessage", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }
}

