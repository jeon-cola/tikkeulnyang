package com.c107.bucket.controller;

import com.c107.bucket.dto.BucketCategoryResponseDto;
import com.c107.bucket.dto.BucketListResponseDto;
import com.c107.bucket.dto.BucketResponseDto;
import com.c107.bucket.service.BucketService;
import com.c107.common.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bucket")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService bucketService;

    // 버킷리스트 카테고리 가져오기
    @GetMapping("/category")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<BucketCategoryResponseDto> categories = bucketService.getAllCategories();
        return ResponseUtil.success("카테고리 목록 조회 성공", categories);
    }

    // 버킷리스트 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBucket(
            @AuthenticationPrincipal String email,
            @RequestBody BucketResponseDto.Request request) {

        BucketResponseDto.Response response = bucketService.createBucket(email, request);

        return ResponseUtil.success("버킷리스트 항목이 성공적으로 생성되었습니다.", response);
    }


    // 버킷리스트 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBucketLists(
            @AuthenticationPrincipal String email) {
        List<BucketListResponseDto> bucketLists = bucketService.getBucketLists(email);
        return ResponseUtil.success("버킷리스트 조회 성공", Map.of("bucket_lists", bucketLists));
    }

    // 버킷리스트 삭제
    @DeleteMapping("/delete/{bucket_id}")
    public ResponseEntity<Map<String, Object>> deleteBucket(
            @AuthenticationPrincipal String email,
            @PathVariable("bucket_id") Integer bucketId) {
        bucketService.deleteBucket(email, bucketId);
        return ResponseUtil.success("버킷리스트 삭제 성공", null);
    }
}