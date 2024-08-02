package com.sjprogramming.restapi.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sjprogramming.restapi.entity.Students;
import com.sjprogramming.restapi.service.ProductService;





@CrossOrigin
@RestController
public class StudentController {
    @Autowired
    private ProductService productService;

    @PostMapping("/saveProduct")
    public ResponseEntity<?> saveProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("status") String status,
            @RequestParam("image") MultipartFile image) {
        try {
            Students product = new Students();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setStatus(status);

            if (!image.isEmpty()) {
                String imagePath = saveImageToFileSystem(image);
                product.setImagePath(imagePath);
                product.setImageUrl("http://localhost:8080/product/" + imagePath);
            }

            Students savedProduct = productService.saveProduct(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Image processing error", HttpStatus.BAD_REQUEST);
        }
    }

    private String saveImageToFileSystem(MultipartFile image) throws IOException {
        String folderPath = "C:\\Users\\RamyaAmbati\\Desktop\\images";
        Path directoryPath = Paths.get(folderPath);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = directoryPath.resolve(fileName);

        Files.write(filePath, image.getBytes());

        return fileName;
    }





@GetMapping("/products")
public ResponseEntity<?> getAllProduct() {
    return new ResponseEntity<>(productService.getAllProduct(), HttpStatus.OK);
}

@GetMapping("/{id}")
public ResponseEntity<?> getProductById(@PathVariable Integer id) {
    return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
}


    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }
    
    @GetMapping("/product/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("C:\\Users\\RamyaAmbati\\Desktop\\images", fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @PostMapping("/editProduct/{id}")
    public ResponseEntity<?> editProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @PathVariable Integer id) {
        try {
            Students existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            existingProduct.setProductName(productName);
            existingProduct.setDescription(description);
            existingProduct.setPrice(price);
            existingProduct.setStatus(status);

            if (image != null && !image.isEmpty()) {
            	 deleteImageFile(existingProduct.getImagePath());
                String imagePath = saveImageToFileSystem(image);
                existingProduct.setImagePath(imagePath);
                existingProduct.setImageUrl("http://localhost:8080/product/" + imagePath);
            }

            Students updatedProduct = productService.editProduct(existingProduct, id);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Image processing error", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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