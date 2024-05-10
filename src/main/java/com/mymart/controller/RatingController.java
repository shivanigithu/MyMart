package com.mymart.controller;

import com.mymart.model.Product;
import com.mymart.model.Rating;
import com.mymart.model.Review;
import com.mymart.model.User;
import com.mymart.repository.ProductsRepository;
import com.mymart.repository.RatingRepository;
import com.mymart.repository.ReviewRepository;
import com.mymart.repository.UserRepository;
import com.mymart.service.CustomUserDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.util.List;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private final ProductsRepository productRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public RatingController(ProductsRepository productRepository, UserRepository userRepository, RatingRepository ratingRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/form")
    public String showRatingForm(@RequestParam("productId") int productId, Model model) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            
            return "error"; 
        }

        model.addAttribute("product", product);
        return "rating";
    }

    @PostMapping("/rate")
    public String rateProduct(@RequestParam("productId") int productId, @RequestParam("rating") double rating,
                              @RequestParam("review") String review, RedirectAttributes redirectAttributes) {
       
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            userId = userDetails.getUser().getId();
        } else {
            throw new IllegalStateException("No authenticated user found.");
        }
        
        
        List<Rating> existingRatings = ratingRepository.findByUserIdAndProductId(userId, Long.valueOf(productId));
        if (!existingRatings.isEmpty()) {
            
            redirectAttributes.addFlashAttribute("message", "You have already rated this product.");
            return "redirect:/rating/form?productId=" + productId;
        }
       
        Long productIdLong = Long.valueOf(productId);

        
        Rating newRating = new Rating();
        newRating.setProductId(productIdLong);
        newRating.setUserId(userId);
        newRating.setRatingValue(rating);
        ratingRepository.save(newRating);

        
        Review newReview = new Review();
        newReview.setProductId(productIdLong);
        newReview.setUserId(userId);
        newReview.setReviewText(review);
        reviewRepository.save(newReview);
        
        redirectAttributes.addFlashAttribute("message", "Submitted successfully");
        
        return "redirect:/rating/form?productId=" + productId;
    }

    @GetMapping("/product/{productId}/ratings")
    @ResponseBody
    public List<Rating> getProductRatings(@PathVariable int productId) {
        return ratingRepository.findByProductId(productId);
    }

    @GetMapping("/user/{userId}/ratings")
    @ResponseBody
    public List<Rating> getUserRatings(@PathVariable Long userId) {
        return ratingRepository.findByUserId(userId);
    }

    @GetMapping("/product/{productId}/reviews")
    @ResponseBody
    public List<Review> getProductReviews(@PathVariable int productId) {
        return reviewRepository.findByProductId(productId);
    }

    @GetMapping("/user/{userId}/reviews")
    @ResponseBody
    public List<Review> getUserReviews(@PathVariable Long userId) {
        return reviewRepository.findByUserId(userId);
    }

}



