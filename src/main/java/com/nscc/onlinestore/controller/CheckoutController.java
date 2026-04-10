package com.nscc.onlinestore.controller;


import com.nscc.onlinestore.dto.CartDTO;
import com.nscc.onlinestore.dto.CartItemDTO;
import com.nscc.onlinestore.dto.LineItemCreateDTO;
import com.nscc.onlinestore.dto.PurchaseCreateDTO;
import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.entity.Purchase;
import com.nscc.onlinestore.service.ProductService;
import com.nscc.onlinestore.service.PurchaseService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@CrossOrigin // enables cors (cross-origin resource sharing)
@RestController
@RequestMapping("/checkout")

public class CheckoutController {
    private final ProductService productService;
    private final PurchaseService purchaseService;


    public CheckoutController(ProductService productService, @Value("${stripe.api.key}") String STRIPE_KEY, PurchaseService purchaseService){

        Stripe.apiKey = STRIPE_KEY;
        this.purchaseService = purchaseService;
        this.productService = productService;
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
            Long price = product.getProdPrice() *100;

            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(cartItem.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("cad")
                                            .setUnitAmount(price)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(product.getProdName())
                                                            .build()
                                            ).build()
                            ).build()
            );
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

    //UPDATE HERE
    @PostMapping("/confirmation")
    public void confirmPayment(@RequestBody CartDTO cart) {
        PurchaseCreateDTO purchaseDTO = new PurchaseCreateDTO();
        Purchase purchase = purchaseService.createPurchase(purchaseDTO);

        for (CartItemDTO item: cart.getItems()){
            LineItemCreateDTO dto = new LineItemCreateDTO();
            dto.setProductId(item.getItemID());
            dto.setQuantity(item.getQuantity().intValue());

            purchaseService.addLineItemToPurchase(purchase.getPurchaseID(), dto);
        }
        purchaseService.markPurchasePaid(purchase.getPurchaseID());
        System.out.println("CONFIRMATION HIT");
        System.out.println(cart);
    }
}
