package com.nscc.onlinestore.controller;

import com.nscc.onlinestore.dto.LineItemCreateDTO;
import com.nscc.onlinestore.dto.PurchaseCreateDTO;
import com.nscc.onlinestore.entity.Purchase;
import com.nscc.onlinestore.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/")
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public Purchase getPurchaseById(@PathVariable long id) {
        return purchaseService.getPurchaseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/add")
    public Purchase updatePurchaseAdd(
            @PathVariable Long id,
            @RequestBody LineItemCreateDTO lineItemDTO) {
        return purchaseService.updatePurchaseAdd(id, lineItemDTO);
    }
    // DELETE: /purchase/{purchaseId}/remove
    @DeleteMapping("/{id}/remove")
    public Purchase removeLineItem(
            @PathVariable Long id,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        return purchaseService.updatePurchaseDelete(id, productId, quantity);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Purchase createPurchase(@Valid @RequestBody PurchaseCreateDTO purchaseDTO) {
        return purchaseService.createPurchase(purchaseDTO);
    }

    @PostMapping("/addlineitem")
    public Purchase addLineItem(@RequestBody LineItemCreateDTO lineItemDTO, @RequestParam Long purchaseId) {
        return purchaseService.addLineItemToPurchase(purchaseId, lineItemDTO);
    }

    @PutMapping("/{id}/pay")
    public Purchase markPaid(@PathVariable Long id) {
        return purchaseService.markPurchasePaid(id);
    }
}
