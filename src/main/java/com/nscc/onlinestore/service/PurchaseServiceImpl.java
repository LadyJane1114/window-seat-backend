package com.nscc.onlinestore.service;

import com.nscc.onlinestore.dto.LineItemCreateDTO;
import com.nscc.onlinestore.dto.PurchaseCreateDTO;
import com.nscc.onlinestore.entity.LineItem;
import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.entity.Purchase;
import com.nscc.onlinestore.repository.ProductRepository;
import com.nscc.onlinestore.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
                               ProductRepository productRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public Optional<Purchase> getPurchaseById(Long id) {
        return purchaseRepository.findById(id);
    }

    @Transactional
    @Override
    public Purchase updatePurchaseAdd(Long purchaseId, LineItemCreateDTO dto) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Check available stock
        if (dto.getQuantity() > product.getProdStockCount()) {
            throw new IllegalStateException("Cannot add more than available stock: " + product.getProdName());
        }

        // Check if the line item already exists
        Optional<LineItem> existingItem = purchase.getLineItems().stream()
                .filter(li -> li.getProduct().getProdID().equals(product.getProdID()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Increment quantity
            LineItem li = existingItem.get();
            int newQuantity = li.getQuantity() + dto.getQuantity();
            if (newQuantity > product.getProdStockCount()) {
                throw new IllegalStateException("Total quantity exceeds available stock for " + product.getProdName());
            }
            li.setQuantity(newQuantity);
        } else {
            // Add new line item
            addLineItem(purchase, product.getProdID(), dto.getQuantity());
        }

        // Recalculate total
        long total = purchase.getLineItems().stream()
                .mapToLong(li -> (long) (li.getUnitPrice() * li.getQuantity()))
                .sum();
        purchase.setPurchaseTotal(total);

        return purchaseRepository.save(purchase);
    }

    @Transactional
    @Override
    public Purchase updatePurchaseDelete(Long purchaseId, Long productId, int quantity) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        Optional<LineItem> itemOpt = purchase.getLineItems().stream()
                .filter(li -> li.getProduct().getProdID().equals(productId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            throw new IllegalStateException("Product not found in purchase");
        }

        LineItem item = itemOpt.get();

        if (quantity >= item.getQuantity()) {
            // Remove completely
            purchase.getLineItems().remove(item);
        } else {
            // Decrease quantity
            item.setQuantity(item.getQuantity() - quantity);
        }

        // Recalculate total
        long total = purchase.getLineItems().stream()
                .mapToLong(li -> (long) (li.getUnitPrice() * li.getQuantity()))
                .sum();
        purchase.setPurchaseTotal(total);

        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public Purchase createPurchase(PurchaseCreateDTO purchaseDTO) {
        Purchase purchase = new Purchase();
        purchase.setPurchaseIsPaid(false);
        purchase.setPurchaseDateTime(LocalDateTime.now());

        for (LineItemCreateDTO itemDTO : purchaseDTO.getItems()) {
            addLineItem(purchase, itemDTO.getProductId(), itemDTO.getQuantity());
        }

        // Set total
        long total = purchase.getLineItems().stream()
                .mapToLong(li -> li.getUnitPrice() * li.getQuantity())
                .sum();
        purchase.setPurchaseTotal(total);

        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public Purchase addLineItemToPurchase(Long purchaseId, LineItemCreateDTO lineItemDTO) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        addLineItem(purchase, lineItemDTO.getProductId(), lineItemDTO.getQuantity());

        // Update total
        long total = purchase.getLineItems().stream()
                .mapToLong(li -> li.getUnitPrice() * li.getQuantity())
                .sum();
        purchase.setPurchaseTotal(total);

        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public Purchase markPurchasePaid(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        if (purchase.isPurchaseIsPaid()) {
            return purchase;
        }

        for (LineItem item : purchase.getLineItems()) {
            int updated = productRepository.decrementStock(
                    item.getProduct().getProdID(),
                    item.getQuantity()
            );

            if (updated == 0) {
                throw new IllegalStateException(
                        "Insufficient stock for product: "
                                + item.getProduct().getProdName());
            }
        }

        purchase.setPurchaseIsPaid(true);

        return purchaseRepository.save(purchase);
    }

    private void addLineItem(Purchase purchase, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        if (product.getProdStockCount() < quantity) {
            throw new IllegalStateException("Not enough stock for product: " + product.getProdName());
        }

        // Create LineItem
        LineItem lineItem = new LineItem();
        lineItem.setProduct(product);
        lineItem.setQuantity(quantity);
        lineItem.setUnitPrice(product.getProdPrice());

        // Add to Purchase
        purchase.addLineItem(lineItem);
    }
}
