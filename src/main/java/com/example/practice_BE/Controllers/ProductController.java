package com.example.practice_BE.Controllers;

import com.example.practice_BE.DTO.ProductRequestDTO;
import com.example.practice_BE.Entity.ProductEntity;
import com.example.practice_BE.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/service")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequestDTO productRequest){
        try {
            ProductEntity createproduct = productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "เพิ่มสินค้าสำเร็จ",
                    "data", createproduct
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "responseStatus", 400,
                    "responseMessage", e.getMessage()
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProduct() {
        try {
            List<ProductEntity> products = productService.findAll(Sort.by(Sort.Direction.ASC, "productId"));
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ดึงข้อมูลสินค้าทั้งหมดสำเร็จ",
                    "data", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "responseStatus", 500,
                    "responseMessage", "เกิดข้อผิดพลาดในระบบ"
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable long id){
        try {
            ProductEntity product = productService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ค้นหาสำเร็จ",
                    "data", product
            ));
        } catch (NoSuchElementException e){
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

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO request) {
        try {
            ProductEntity updated = productService.updateProduct(id, request);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "อัปเดตสินค้าสำเร็จ",
                    "data", updated
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
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of(
                    "responseStatus", 200,
                    "responseMessage", "ลบสินค้าสำเร็จ"
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
