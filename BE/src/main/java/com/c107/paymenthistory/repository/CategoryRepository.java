package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByCategoryCode(String categoryCode);
    List<CategoryEntity> findAllByMerchantName(String merchantName);
    List<CategoryEntity> findByMerchantName(String merchantName);

    List<CategoryEntity> findByMerchantNameContaining(String merchantNamePart);

}