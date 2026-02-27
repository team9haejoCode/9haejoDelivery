package com.sparta._9haejodelivery.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID productId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(length = 50, nullable = false)
  private String productName;

  @Column(length = 100)
  private String description;

  private Integer price;

  @Column(columnDefinition = "TEXT")
  private String image;

  private Boolean isSoldout = false;
}