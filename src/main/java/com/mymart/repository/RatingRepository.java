package com.mymart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mymart.model.Rating;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Integer productId);

    List<Rating> findByUserId(Long userId);
    
    List<Rating> findByUserIdAndProductId(Long userId, Long productId);
    
    List<Rating> findByProductId(Long productId);
}


