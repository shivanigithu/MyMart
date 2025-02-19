package com.mymart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.mymart.model.ContactUs;
import com.mymart.model.Faq;
import com.mymart.model.Product;
import com.mymart.model.Rating;
import com.mymart.repository.ContactUsRepository;
import com.mymart.repository.FaqRepository;
import com.mymart.repository.ProductsRepository;
import com.mymart.repository.RatingRepository;
import com.mymart.service.DealService;

@Controller
public class NavbarController {
	
	 @Autowired
	 DealService dealService;
	 
	 @Autowired
	 ProductsRepository repo;
	 
	 @Autowired
	 ContactUsRepository contactRepo;
	 @Autowired
	    private FaqRepository faqRepository;

	 @Autowired
     private RatingRepository ratingRepository;  

	 @GetMapping("/Admin/Deals")
	    public String adminPage(Model model) {
		    model.addAttribute("products", dealService.getProductsWithDiscountDeals());

	        return "products/AdminDeals";
	    }
	 @GetMapping("/Deals")
	    public String adminPagea(Model model) {
		    model.addAttribute("products", dealService.getProductsWithDiscountDeals());
		    List<Product> products = repo.findAll();
	        for (Product product : products) {
	            calculateAndSetAverageRating(product);
	        }
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
	 @GetMapping("/Contact")
	 public String userContact(Model model) {
	        ContactUs latestContact = contactRepo.findFirstByOrderByIdDesc();
	        model.addAttribute("contactUs", latestContact);
	        return "Contact";
	    }
	 @GetMapping("/Admin/Contact")
	    public String adminContact(Model model) {
	        model.addAttribute("contactUs", new ContactUs());
	        return "admincontact";
	    }
	 @PostMapping("/admincontact") // Add this mapping for handling POST requests
	    public String adminContactSubmit(@ModelAttribute ContactUs contactUs) {
	        contactRepo.save(contactUs);
	        return "redirect:/Admin/Contact";
	    }
	 @GetMapping("/Admin/Faqs")
	    public String faqPage(Model model) {
	        model.addAttribute("faqs", faqRepository.findAll());
	        return "faq";
	    }
	    
	    @GetMapping("/Faqs")
	    public String faqUserPage(Model model) {
	        model.addAttribute("faqs", faqRepository.findAll());
	        return "Aq";
	    }

	    @GetMapping("/faq/new")
	    public String newFaq(Model model) {
	        model.addAttribute("faq", new Faq());
	        return "new_faq";
	    }

	    @PostMapping("/faq/save")
	    public String saveFaq(@ModelAttribute Faq faq) {
	        faqRepository.save(faq);
	        return "redirect:/Admin/Faqs";
	    }

	    @GetMapping("/faq/edit/{id}")
	    public String editFaq(@PathVariable Long id, Model model) {
	        Optional<Faq> faq = faqRepository.findById(id);
	        faq.ifPresent(value -> model.addAttribute("faq", value));
	        return "edit_faq";
	    }

	    @PostMapping("/faq/update/{id}")
	    public String updateFaq(@PathVariable Long id, @ModelAttribute Faq updatedFaq) {
	        Optional<Faq> optionalFaq = faqRepository.findById(id);
	        optionalFaq.ifPresent(faq -> {
	            faq.setQuestion(updatedFaq.getQuestion());
	            faq.setAnswer(updatedFaq.getAnswer());
	            faqRepository.save(faq);
	        });
	        return "redirect:/Admin/Faqs";
	    }

	    @PostMapping("/faq/delete/{id}")
	    public String deleteFaq(@PathVariable Long id) {
	        faqRepository.deleteById(id);
	        return "redirect:/Admin/Faqs";
	    }
}
