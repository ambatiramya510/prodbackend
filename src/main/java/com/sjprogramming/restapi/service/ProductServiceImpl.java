package com.sjprogramming.restapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sjprogramming.restapi.entity.Students;
import com.sjprogramming.restapi.repository.StudentRepository;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private StudentRepository productRepo;

    private final String imageBaseUrl = "http://localhost:8080/product/"; 
//    private final String imageFolderPath = "C:\\Users\\RamyaAmbati\\Desktop\\images"; 

    @Override
    public Students saveProduct(Students product) {
        return productRepo.save(product);
    }

    @Override
    public List<Students> getAllProduct() {
        List<Students> products = productRepo.findAll();
        products.forEach(this::setImageUrl);
        return products;
    }

    @Override
    public Students getProductById(Integer id) {
        Students product = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        setImageUrl(product);
        return product;
    }

    @Override
    public String deleteProduct(Integer id) {
        Optional<Students> productOpt = productRepo.findById(id);
        if (productOpt.isPresent()) {
            Students product = productOpt.get();
            deleteImageFile(product.getImagePath());
            productRepo.delete(product);
            return "Product Deleted Successfully";
        }
        return "Product not found";
    }

    @Override
    public Students editProduct(Students product, Integer id) {
        Students oldProduct = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        
        oldProduct.setProductName(product.getProductName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStatus(product.getStatus());

        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            deleteImageFile(oldProduct.getImagePath()); 
            oldProduct.setImagePath(product.getImagePath());
        }

        Students updatedProduct = productRepo.save(oldProduct);
        setImageUrl(updatedProduct);
        return updatedProduct;
    }

    private void setImageUrl(Students product) {
        if (product.getImagePath() != null) {
            product.setImageUrl(imageBaseUrl + product.getImagePath());
        }
    }



    private void deleteImageFile(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Path path = Paths.get("C:\\Users\\RamyaAmbati\\Desktop\\images", imagePath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
               
                System.err.println("Failed to delete image file: " + imagePath);
                e.printStackTrace();
            }
        }
    }

}