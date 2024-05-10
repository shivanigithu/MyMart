package com.mymart.controller;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.mymart.model.Address;
import com.mymart.model.CartItem;
import com.mymart.model.Orders;
import com.mymart.model.PaymentCallbackRequest;
import com.mymart.model.RazorPay;
import com.mymart.model.Response;
import com.mymart.model.User;
import com.mymart.repository.AddressRepository;
import com.mymart.service.AddressService;
import com.mymart.service.CartItemService;
import com.mymart.service.OrderService;
import com.mymart.service.ProductService;
import com.mymart.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
@Controller

public class PaymentController {

	@Autowired
	private UserService userService;
	 @Autowired
	    private ProductService productService;
	
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	private AddressService addressService;
	
	
	@Autowired
	private CartItemService cartService;
	@Autowired
    private OrderService orderService;
	private RazorpayClient client;
	private static Gson gson = new Gson();
	
	private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

	private static final String SECRET_ID = "rzp_test_bwM2xNhKd85xRB";
	private static final String SECRET_KEY = "xsKkMUfuHoW45FrWCwHFGPNv";
	
	public PaymentController() throws RazorpayException {
		this.client =  new RazorpayClient(SECRET_ID, SECRET_KEY); 
	}
	
	
	@RequestMapping(value="/payment")
	public String getHomeInit(@RequestParam("addressId") Integer addressId, Model model, Principal principal) {
		 String username = principal.getName();

	      
	        User user = userService.findByEmail(username); 
	        List<CartItem> cartItems = cartItemService.getAllCartItemsByUser(user);

	      
	        double subtotal = cartItemService.calculateSubtotal(cartItems);
	        double shipping = cartItemService.calculateShipping(subtotal);
	        double total = subtotal + shipping;

	        
	        model.addAttribute("subtotal", subtotal);
	        model.addAttribute("shipping", shipping);
	        model.addAttribute("total", total);
	        model.addAttribute("user", user);

	        Optional<Address> optionalAddress = addressRepository.findById(addressId);
	        if (optionalAddress.isPresent()) {
	            model.addAttribute("selectedAddress", optionalAddress.get());
	            return "payment"; 
	        } else {
	            return "redirect:/error"; 
	        }
	    }
		
	@RequestMapping(value="/createPayment", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createOrder(@RequestBody Orders orders) {
			
		try {
		
			
			Order order = createRazorPayOrder( orders.getAmount() );
			RazorPay razorPay = getRazorPay((String)order.get("id"), orders);
			
			
			
			return new ResponseEntity<String>(gson.toJson(getResponse(razorPay, 200)),
					HttpStatus.OK);
		} catch (RazorpayException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>(gson.toJson(getResponse(new RazorPay(), 500)),
				HttpStatus.EXPECTATION_FAILED);
	}
	
	
	
	
	
	private String processPayment(Orders order) {
        return "TXN123456789";
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }
	
	private Response getResponse(RazorPay razorPay, int statusCode) {
		Response response = new Response();
		response.setStatusCode(statusCode);
		response.setRazorPay(razorPay);
		return response;
	}	
	
	private RazorPay getRazorPay(String orderId, Orders orders) {
		RazorPay razorPay = new RazorPay();
		razorPay.setApplicationFee(convertRupeeToPaise(orders.getAmount()));

		razorPay.setPurchaseDescription("PRODUCT PURCHASES");
		razorPay.setRazorpayOrderId(orderId);
		razorPay.setSecretKey(SECRET_ID);
		razorPay.setTheme("white");
		razorPay.setMerchantName("MyMart");
		razorPay.setNotes("notes"+orderId);
		
		return razorPay;
	}
	
	private Order createRazorPayOrder(String amount) throws RazorpayException {
		
		JSONObject options = new JSONObject();
		options.put("amount", convertRupeeToPaise(amount));
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		options.put("payment_capture", 1);  
		return client.orders.create(options);
	}
	

	
	private String convertRupeeToPaise(String paise) {
	    if (paise == null || paise.isEmpty()) {
	        throw new IllegalArgumentException("Invalid input: amount cannot be empty or null.");
	    }
	    BigDecimal b = new BigDecimal(paise);
	    BigDecimal value = b.multiply(new BigDecimal("100"));
	    return value.setScale(0, RoundingMode.UP).toString();
	}

	
	  @PostMapping("/storeFormData")
	    public ResponseEntity<String> storeFormData(@PathVariable int addressId, @RequestBody Orders orders) {
	        try {
	        	
	          
	            orderService.saveOrder(orders);

	            return new ResponseEntity<>("Form data stored successfully", HttpStatus.OK);
	        } catch (Exception e) {
	            return new ResponseEntity<>("Error storing form data", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	
		
	 
	

}

	
