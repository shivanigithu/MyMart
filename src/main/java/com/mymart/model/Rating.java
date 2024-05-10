package com.mymart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "ratings", uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "user_id"})})
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "rating_value")
    private double ratingValue;

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

	public double getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(double ratingValue) {
		this.ratingValue = ratingValue;
	}

	public Rating(Long id, Long productId, Long userId, double ratingValue) {
		super();
		this.id = id;
		this.productId = productId;
		this.userId = userId;
		this.ratingValue = ratingValue;
	}

	public Rating() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Rating [id=" + id + ", productId=" + productId + ", userId=" + userId + ", ratingValue=" + ratingValue
				+ "]";
	}
    
}
