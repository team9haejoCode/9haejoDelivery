package com.sparta._9haejodelivery.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_REGION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID regionId;

  @Column(length = 20, nullable = false)
  private String city;

  @Column(length = 20)
  private String district;

  @Column(length = 20)
  private String neighborhood;

  @Column(length = 10)
  private String postalCode;

  private Boolean isServiced = true;
}