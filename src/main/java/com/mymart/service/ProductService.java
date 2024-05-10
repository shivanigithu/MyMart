package com.mymart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mymart.model.Category;
import com.mymart.model.Product;
import com.mymart.repository.ProductsRepository;

@Service
public class ProductService {


	    @Autowired
	    public ProductService(ProductsRepository productRepository) {
	        this.productRepository = productRepository;
	       
	    }
	private final ProductsRepository productRepository;

	

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }



    public Product getProductById(int productId) {
        return productRepository.findById(productId).orElse(null);
    }
    
    public List<Product> searchProductss(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, query, query);
    }
    
   
    
    public List<Product> searchProducts(String query) {
        return productRepository.findBySearchQuery(query);
    }



    public void save(Product product) {
        productRepository.save(product);
    }



	 public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    
}
   
