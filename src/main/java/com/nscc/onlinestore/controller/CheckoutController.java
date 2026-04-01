package com.nscc.onlinestore.controller;


import com.nscc.onlinestore.dto.CartDTO;
import com.nscc.onlinestore.dto.CartItemDTO;
import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.service.ProductService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@CrossOrigin // enables cors (cross-origin resource sharing)
@RestController
@RequestMapping("/checkout")

public class CheckoutController {
    private final ProductService productService;


    public CheckoutController(ProductService productService){
        this.productService = productService;

        Stripe.apiKey = "sk_test_51T2GDsDlTUkLxqzESPEbfwEOV2F1n1ByAQfKvOzn7RV06AriJU1DM4s7r1HXBcDOyDtX6ainCzywhKP2zhhlzbby00jNgGQy62";
    }

//post mapping
    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody CartDTO cart) throws StripeException {
        String FRONTEND_DOMAIN = "http://localhost:5173";

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                        .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setReturnUrl(FRONTEND_DOMAIN + "/confirmation?session_id={CHECKOUT_SESSION_ID}");
//        changed to build incrementally instead of all at once
        for (CartItemDTO cartItem: cart.getItems()){
            // get product
            Product product = productService.getProductByID(cartItem.getItemID())
                    .orElseThrow(()-> new RuntimeException("Product not found!"));
            //get the price
            Long price = product.getProdPrice();

            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(cartItem.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.builder(

                                    )
                            )
            )
        }
        SessionCreateParams params = paramsBuilder.build();
        Session session = Session.create(params);


        System.out.println("Session ID: " + session.getId());

        Map<String, String> map = new HashMap<>();
        map.put("clientSecret", session.getClientSecret());

        return map;
    }

//    get mapping
    @GetMapping("/session-status")
    public Map<String,String> getSessionStatus(@RequestParam String session_id) throws StripeException {
        Session session = Session.retrieve(session_id);

        Map<String, String> map = new HashMap<>();
        map.put("status", session.getStatus());
        map.put("customer_email", session.getCustomerDetails().getEmail());

        return map;
    }
}
