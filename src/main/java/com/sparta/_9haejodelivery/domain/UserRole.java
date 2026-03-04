package com.sparta._9haejodelivery.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    CUSTOMER("ROLE_CUSTOMER"),
    OWNER("ROLE_OWNER"),
    MANAGER("ROLE_MANAGER"),
    MASTER("ROLE_MASTER");

    private final String authority;
}