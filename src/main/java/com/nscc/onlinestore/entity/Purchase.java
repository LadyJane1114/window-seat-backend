package com.nscc.onlinestore.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseID;

//    @NotBlank - this will be needed later but because we aren't in the stripe phase I'm commenting it out so it doesn't mess with my testing
    @Column(nullable = true)
    private String stripeSessionID;

//    Purchase status
    private boolean purchaseIsPaid;

    @DecimalMin("0.0")
    private Long purchaseTotal;

    private LocalDateTime purchaseDateTime;

    //Relationship
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LineItem> lineItems = new ArrayList<>();

    //Helper
    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setPurchase(this); // maintain bidirectional relationship
    }
}
