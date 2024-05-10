package com.mymart.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mymart.ProductFilter;
import com.mymart.model.Category;
import com.mymart.model.Deal;
import com.mymart.model.Filter;
import com.mymart.model.Product;
import com.mymart.model.ProductDto;
import com.mymart.model.Rating;
import com.mymart.repository.ProductsRepository;
import com.mymart.repository.RatingRepository;
import com.mymart.service.CategoryService;
import com.mymart.service.DealService;
import com.mymart.service.FilterService;
import com.mymart.service.ProductService;

@Controller
@RequestMapping("/User")

public class UserProductsController {
	@Autowired
	private ProductsRepository repo;
	 @Autowired
	 private  DealService dealService;

	 @Autowired
	 private FilterService filterService;
	 @Autowired
      private RatingRepository ratingRepository;  
	
	private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public UserProductsController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("{categoryName}")
    public String displayProductsByCategory(@PathVariable String categoryName, Model model) {
        Category category = categoryService.getCategoryByName(categoryName);
        if (category == null) {
            return "error";
        }
        List<Product> products = productService.getProductsByCategory(category);
        for (Product product : products) {
            calculateAndSetAverageRating(product);
        }
        List<Deal> deals = dealService.getAllDeals();
        model.addAttribute("deals", deals);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "products/UserProduct"; 
    }

    private void calculateAndSetAverageRating(Product product) {
        List<Rating> ratings = ratingRepository.findByProductId(product.getId());

        if (!ratings.isEmpty()) {
            double totalRating = 0.0;
            for (Rating rating : ratings) {
                totalRating += rating.getRatingValue();
            }
            double averageRating = totalRating / ratings.size();
            product.setAverageRating(averageRating);
            product.setRatingCount(ratings.size());
        } else {
            product.setAverageRating(0.0);
            product.setRatingCount(0);
            
        }
    }



    @GetMapping("/All Products")
    public String showProductList(Model model) {      
        List<Product> products = repo.findAll();
        for (Product product : products) {
            calculateAndSetAverageRating(product);
        }
        model.addAttribute("products", products);
        
        List<Deal> deals = dealService.getAllDeals();
        model.addAttribute("deals", deals);
        
        return "products/UserProduct";
    }
	 @GetMapping("/viewproduct")
		public String showEditPage1(
				Model model,
				@RequestParam int id
				) {
			try {
				model.addAttribute("categories", categoryService.getAllCategories());

				Product product = repo.findById(id).get();
				model.addAttribute("product",product);
				
				ProductDto productDto = new ProductDto();
				productDto.setName(product.getName());
				productDto.setBrand(product.getBrand());
			    productDto.setCategory(product.getCategory());
				
				

				productDto.setPrice(product.getPrice());
				productDto.setDescription(product.getDescription());

				model.addAttribute("productDto",productDto);
				

			}
			catch(Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
				return "redirect:/Products";
			}
			return "products/viewproduct";
		}
	 
	 
}
