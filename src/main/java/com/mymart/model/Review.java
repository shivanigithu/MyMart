package com.mymart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "review_text", length = 1000) 
    private String reviewText;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public Review(Long id, Long productId, Long userId, String reviewText) {
		super();
		this.id = id;
		this.productId = productId;
		this.userId = userId;
		this.reviewText = reviewText;
	}

	public Review() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Review [id=" + id + ", productId=" + productId + ", userId=" + userId + ", reviewText=" + reviewText
				+ "]";
	}
    
}

