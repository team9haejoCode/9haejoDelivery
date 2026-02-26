package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_STORE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreEntity {

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
}
