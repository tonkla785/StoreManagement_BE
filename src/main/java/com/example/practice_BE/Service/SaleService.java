package com.example.practice_BE.Service;

import com.example.practice_BE.DTO.SaleDetailRequestDTO;
import com.example.practice_BE.Entity.ProductEntity;
import com.example.practice_BE.Entity.SaleDetailEntity;
import com.example.practice_BE.Entity.SaleEntity;
import com.example.practice_BE.Repository.ProductRepository;
import com.example.practice_BE.Repository.SaleDetailRepository;
import com.example.practice_BE.Repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository,
                       SaleDetailRepository saleDetailRepository,
                       ProductService productService,
                       ProductRepository productRepository
                       ){
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    //create bill
    @Transactional
    public SaleEntity createSale(List<SaleDetailRequestDTO> saleDetails){
        try {
            validateSaleDetail(saleDetails);

            double total = 0.0;
            List<SaleDetailEntity> detailEntities = new ArrayList<>();

            SaleEntity sale = new SaleEntity();
            sale.setSaleTokenId(UUID.randomUUID().toString());
            sale.setSaleDate(new Timestamp(System.currentTimeMillis()));
            sale.setSaleTotal(total);
            saleRepository.save(sale);

            List<ProductEntity> updatedProducts = new ArrayList<>();

            for (SaleDetailRequestDTO dto : saleDetails) {
                ProductEntity product = productService.findById(dto.getProductId());

                if (product.getProductAmount() < dto.getQuantity()) {
                    throw new IllegalArgumentException("Stock ไม่พอสำหรับสินค้า " + product.getProductName() +
                            " (เหลืออยู่: " + product.getProductAmount() + ")");
                }

                product.setProductAmount(product.getProductAmount()-dto.getQuantity());
                updatedProducts.add(product);

                SaleDetailEntity detail = new SaleDetailEntity();
                detail.setSaleId(sale);
                detail.setProductId(product);
                detail.setQuantitySale(dto.getQuantity());

                double price = product.getProductPrice() * dto.getQuantity();
                detail.setPriceSale(price);
                total += price;

                sale.setSaleName(dto.getBillName());

                detailEntities.add(detail);
            }

            productRepository.saveAll(updatedProducts);
            saleDetailRepository.saveAll(detailEntities);

            sale.setSaleTotal(total);
            saleRepository.save(sale);

            return sale;
        }catch (IllegalArgumentException | NoSuchElementException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Error while Ordering",e);
        }
    }

    //Get All bill
    public List<SaleEntity> findAll(Sort sort){
        try {
            return saleRepository.findAll(sort);
        }catch (Exception e){
            throw new RuntimeException("Error while Searching",e);
        }
    }


    //Get bill by id
    public SaleEntity getSaleById(Long id) {
        try {
            SaleEntity sale = saleRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bill with ID "+id+" not found"));
            sale.getSaleDetails().size();
            return sale;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while searching for Bill",e);
        }

    }

    //Find Bill by name
    public List<SaleEntity> searchSalesByName(String keyword) {
        try{
            return saleRepository.findBySaleNameContainingIgnoreCase(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Error while searching for Bill",e);
        }
    }

    //Update bill
    @Transactional
    public SaleEntity updateSale(Long id, List<SaleDetailRequestDTO> updatedDetails){
        try {
            SaleEntity sale = saleRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("ไม่พบ Sale ID: " + id));

            validateSaleDetail(updatedDetails);

            List<SaleDetailEntity> existingDetails = sale.getSaleDetails();
            List<ProductEntity> productsToUpdate = new ArrayList<>();
            List<SaleDetailEntity> detailsToUpdate = new ArrayList<>();
            List<SaleDetailEntity> detailsToAdd = new ArrayList<>();

            double newTotal = 0.0;

            for (int i = 0; i < existingDetails.size(); i++) {
                SaleDetailEntity existing = existingDetails.get(i);

                boolean stillExists = updatedDetails.stream()
                        .anyMatch(dto -> dto.getProductId().equals(existing.getProductId().getProductId()));

                if (!stillExists) {
                    ProductEntity product = existing.getProductId();
                    product.setProductAmount(product.getProductAmount() + existing.getQuantitySale());
                    productsToUpdate.add(product);

                    saleDetailRepository.delete(existing);
                    existingDetails.remove(i);
                    i--;
                }
            }

            for (SaleDetailRequestDTO dto : updatedDetails) {
                ProductEntity product = productService.findById(dto.getProductId());

                SaleDetailEntity existingDetail = existingDetails.stream()
                        .filter(d -> d.getProductId().getProductId().equals(dto.getProductId()))
                        .findFirst()
                        .orElse(null);

                int newQty = dto.getQuantity();
                int oldQty = existingDetail != null ? existingDetail.getQuantitySale() : 0;
                int availableStock = product.getProductAmount()+oldQty;

                if (existingDetail != null) {
                    int diff = newQty - oldQty;

                    if (availableStock < newQty) {
                        throw new IllegalArgumentException("Stock ไม่พอสำหรับสินค้า " + product.getProductName() +
                                " (เหลืออยู่: " + availableStock + ")");
                    }

                    product.setProductAmount(product.getProductAmount() - diff);
                    productsToUpdate.add(product);

                    existingDetail.setQuantitySale(newQty);
                    existingDetail.setPriceSale(newQty * product.getProductPrice());
                    detailsToUpdate.add(existingDetail);

                    newTotal += existingDetail.getPriceSale();
                } else {
                    if (availableStock < newQty) {
                        throw new IllegalArgumentException("Stock ไม่พอสำหรับสินค้า " + product.getProductName() +
                                " (เหลืออยู่: " + availableStock + ")");
                    }

                    product.setProductAmount(product.getProductAmount() - newQty);
                    productsToUpdate.add(product);

                    SaleDetailEntity newDetail = new SaleDetailEntity();
                    newDetail.setSaleId(sale);
                    newDetail.setProductId(product);
                    newDetail.setQuantitySale(newQty);
                    newDetail.setPriceSale(newQty * product.getProductPrice());

                    detailsToAdd.add(newDetail);
                    existingDetails.add(newDetail);

                    newTotal += newDetail.getPriceSale();
                }

                sale.setSaleName(dto.getBillName());
            }

            productRepository.saveAll(productsToUpdate);
            saleDetailRepository.saveAll(detailsToUpdate);
            saleDetailRepository.saveAll(detailsToAdd);

            sale.setSaleTotal(newTotal);
            saleRepository.save(sale);

            return sale;
        }catch (NoSuchElementException | IllegalArgumentException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while updating Bill",e);
        }
    }

    //Delete bill
    @Transactional
    public void deleteSale(Long id) {
        try {
            SaleEntity sale = saleRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("ไม่พบบิล ID: " + id));

            List<SaleDetailEntity> saleDetails = sale.getSaleDetails();
            List<ProductEntity> productsToUpdate = new ArrayList<>();

            for (SaleDetailEntity detail : saleDetails) {
                ProductEntity product = detail.getProductId();
                int quantity = detail.getQuantitySale();

                product.setProductAmount(product.getProductAmount() + quantity);

                productsToUpdate.add(product);
            }

            productRepository.saveAll(productsToUpdate);
            saleRepository.delete(sale);

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Error while deleting Bill", e);
        }
    }

    //Validate sale
    private void validateSaleDetail(List<SaleDetailRequestDTO> saleDetails){
        for (SaleDetailRequestDTO dto : saleDetails) {
            ProductEntity product = productService.findById(dto.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("Product name " + dto.getProductId() + " ไม่พบในระบบ");
            } else if (dto.getQuantity() == null || dto.getQuantity() <= 0 ) {
                throw new IllegalArgumentException("จำนวนที่สั่งซื้อของสินค้า " + product.getProductName() + " ต้องมากกว่า 0");
            } else if (dto.getProductId() == null) {
                throw new IllegalArgumentException("Product id can not be null");
            } else if (dto.getBillName() == null || dto.getBillName().trim().isEmpty()) {
                throw new IllegalArgumentException("Bill name cannot be null or empty");
            }
        }
    }
}
