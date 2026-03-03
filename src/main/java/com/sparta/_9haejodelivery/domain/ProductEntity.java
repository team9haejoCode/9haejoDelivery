package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "P_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "product_name", length = 50, nullable = false)
    private  String productName;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "is_soldout", nullable = false)
    private Boolean isSoldout;

    @Builder
    public ProductEntity(StoreEntity store, String productName, String description, Integer price, String image, Boolean isSoldout) {
        this.store = store;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image = image;
        this.isSoldout = isSoldout != null ? isSoldout : false;
    }

    public void updateProduct(String productName, String description, Integer price, String image, Boolean isSoldout) {
        if (productName != null) this.productName = productName;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (image != null) this.image = image;
        if (isSoldout != null) this.isSoldout = isSoldout;
    }
}