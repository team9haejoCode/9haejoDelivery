package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="P_AI_LOG")
public class Ai {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID aiLogId;

    @ManyToOne
    private User user;

    @Column(name = "requestData")
    private String requestData;

    @Column(name = "responseData")
    private String responseData;

    @Builder
    public Ai(User user, String requestData, String responseData) {
        this.user = user;
        this.requestData = requestData;
        this.responseData = responseData;
    }
}
