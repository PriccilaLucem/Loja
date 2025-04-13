package org.example.loja.controller;

import org.example.loja.entities.ImageEntity;
import org.example.loja.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    private static final Logger LOGGER = Logger.getLogger(ImageController.class.getName());

    @PostMapping("/upload/{store}/product/{productId}")
    public ResponseEntity<?> uploadImageForProduct(@PathVariable Long productId,
                                                   @RequestParam("file") MultipartFile file,
                                                   @PathVariable String store) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path folderPath = Paths.get("uploads", store);
            Files.createDirectories(folderPath);
            Path filePath = folderPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            ImageEntity image = new ImageEntity();
            image.setFileName(fileName);
            image.setUrl("/uploads/" + store + "/" + fileName);
            Files.copy(file.getInputStream(), filePath);



            String url = imageService.saveProductImage(image, productId, store);

            LOGGER.info("Image successfully uploaded for product ID: " + productId);
            return ResponseEntity.ok(Map.of("response", "Image uploaded successfully", "url", url));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save product image", e);
            return ResponseEntity.badRequest().body(Map.of("response", "Image upload failed"));
        } catch (RuntimeException e) {
            LOGGER.warning("Product not found: ID " + productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("response", "Product not found"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal error while saving product image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("response", "Internal server error"));
        }
    }

    @PostMapping("/upload/store/{store}")
    public ResponseEntity<?> uploadImageForStore(@PathVariable String store,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path folderPath = Paths.get("uploads", store);
            Files.createDirectories(folderPath);
            Path filePath = folderPath.resolve(fileName);

            ImageEntity image = new ImageEntity();
            image.setFileName(fileName);
            image.setUrl("/uploads/" + store + "/" + fileName);
            Files.copy(file.getInputStream(), filePath);

            String url = imageService.saveStoreImage(image, store);

            LOGGER.info("Image successfully uploaded for store: " + store);
            return ResponseEntity.ok(Map.of("response", "Image uploaded successfully", "url", url));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save store image", e);
            return ResponseEntity.badRequest().body(Map.of("response", "Image upload failed"));
        } catch (RuntimeException e) {
            LOGGER.warning("Store not found: ID " + store);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("response", "Store not found"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal error while saving store image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("response", "Internal server error"));
        }
    }
}
