package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class OrderItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID orderItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Integer unitPrice;


  @Builder
  public OrderItem(Product product, Integer quantity, Integer unitPrice) {
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  void assignOrder(Order order) {
    this.order = order;
  }

  public Integer getSubTotal() {
    return this.unitPrice * this.quantity;
  }
}
