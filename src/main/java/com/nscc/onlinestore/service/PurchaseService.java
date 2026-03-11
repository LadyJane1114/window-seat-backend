package com.nscc.onlinestore.service;

import com.nscc.onlinestore.dto.LineItemCreateDTO;
import com.nscc.onlinestore.dto.PurchaseCreateDTO;
import com.nscc.onlinestore.entity.LineItem;
import com.nscc.onlinestore.entity.Purchase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {

    List<Purchase> getAllPurchases();

    Optional<Purchase> getPurchaseById(Long id);

    @Transactional
    Purchase updatePurchaseAdd(Long purchaseId, LineItemCreateDTO dto);

    @Transactional
    Purchase updatePurchaseDelete(Long purchaseId, Long productId, int quantity);

    Purchase createPurchase(PurchaseCreateDTO purchaseDTO);

    @Transactional
    Purchase addLineItemToPurchase(Long purchaseId, LineItemCreateDTO lineItemDTO);

    @Transactional
    Purchase markPurchasePaid(Long purchaseId);
}
