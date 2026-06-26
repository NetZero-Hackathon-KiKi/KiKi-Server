package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Tag(name = "이미지", description = "이미지 업로드 및 Presigned URL API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @Operation(summary = "Presigned URL 발급", description = "S3에 이미지를 업로드할 수 있는 Presigned URL을 발급합니다. 프론트엔드에서 이 URL로 PUT 요청하여 직접 업로드합니다.")
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUrl(
            @Parameter(description = "파일명 (확장자 포함)") @RequestParam String filename,
            @Parameter(description = "업로드 폴더 (quest, attack, profile)") @RequestParam(defaultValue = "quest") String folder) {
        return ResponseEntity.ok(ApiResponse.ok(s3Service.generatePresignedUploadUrl(folder, filename)));
    }

    @Operation(summary = "이미지 직접 업로드", description = "서버를 통해 S3에 이미지를 직접 업로드합니다. 업로드된 이미지의 URL을 반환합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일") @RequestParam("file") MultipartFile file,
            @Parameter(description = "업로드 폴더 (quest, attack, profile)") @RequestParam(defaultValue = "quest") String folder) throws IOException {
        String imageUrl = s3Service.uploadFile(file, folder);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("imageUrl", imageUrl)));
    }
}
