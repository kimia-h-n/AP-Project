//package com.example.sales.picture;
//
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/v1/image")
//public class StorageController {
//    private final StorageService service;
//
//
//    @Transactional
//    @PostMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void uploadImages(@RequestParam("image") MultipartFile file) throws IOException {
//        service.uploadImage(file);
//    }
//
//    @Transactional(readOnly = true)
//    @GetMapping("/{filename}")
//    public ResponseEntity<?> downloadImage(@PathVariable String filename) {
//        System.out.println("Here");
//        byte[] image = service.downloadImage(filename);
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(image);
//    }
//}
