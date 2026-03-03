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
    private CategoryEntity category;

    @Column(name = "region_id", columnDefinition = "uuid")
    private UUID regionId;

    @Column(name = "owner_id", length = 30)
    private String ownerId;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "is_hide")
    private Boolean isHide;

    @Builder
    public Store(String storeName, CategoryEntity category, UUID regionId, String address, String description, Boolean isHide) {
        this.storeName = storeName;
        this.category = category;
        this.regionId = regionId;
        this.address = address;
        this.description = description;
        this.isHide = isHide != null ? isHide : false;
    }

    public void updateStore(String storeName) {
        this.storeName = storeName;
    }
}
