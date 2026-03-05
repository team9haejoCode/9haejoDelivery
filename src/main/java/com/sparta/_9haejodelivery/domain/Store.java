package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "P_STORE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "store_id", columnDefinition = "uuid")
    private UUID storeId;

    @Column(name = "store_name", length = 50, nullable = false)
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "is_hide")
    private Boolean isHide;

    @Builder
    public Store(String storeName, Category category, User owner, Region region, String address, String description, Boolean isHide) {
        this.storeName = storeName;
        this.category = category;
        this.owner = owner;
        this.region = region;
        this.address = address;
        this.description = description;
        this.isHide = isHide != null ? isHide : false;
    }

    public void updateStore(String storeName, Category category, Region region, String address, String description, Boolean isHide) {
        if (storeName != null) this.storeName = storeName;
        if (category != null) this.category = category;
        if (region != null) this.region = region;
        if (address != null) this.address = address;
        if (description != null) this.description = description;
        if (isHide != null) this.isHide = isHide;
    }
}
