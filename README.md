# 🚀 9해줘배달
> 배달 음식 주문 관리 플랫폼
## 핵심 가치

## 역할 분담
| 성명 | 역할 | 담당 기능 |
| :--- | :--- | :--- |
| 여정진 | REVIEW & AI | 리뷰 CRUD, Gemini API 연동 및 프롬프트 엔지니어링 |
| 조하연 | PRODUCT | 상품 CRUD |
| 서민석 | PAYMENT | 결제 CRUD |
| 곽정아 | STORE & COMMON INFRA | STORE CRUD, 공통 응답/예외 처리 |
| 백가은 | USER & AUTH | 회원 관리, JWT/Security 기반 인증/인가 |
| 신단비 | ORDER | ORDER CRUD |
## 서비스 구성 및 실행 방법

##  기술 스택
- **언어:** Java 17
- **프레임워크:** Spring Boot 3.x
- **ORM:** Spring Data JPA(Hibernate)
- **DB:** PostgresSQL
- **인증/인가:** Spring Security + JWT
- **빌드:** Gradle

## ERD
<img width="1835" height="1710" alt="Image" src="https://github.com/user-attachments/assets/df8949d4-b54e-4afe-bef7-be068bdb6a0c" />

## Architecture
<img width="800" alt="9haego_archipng" src="https://github.com/user-attachments/assets/e60cd118-5985-428b-b8f0-642fd5489dce" />

## API 명세서

- 매장, 카테고리 API

  1️⃣ 매장, 카테고리 관리 API

  | Method | Endpoint | 설명 |
            | --- | --- | --- |
  | POST | `/stores` | 매장 등록 |
  | GET | `/stores/{storeId}` | 매장 상세 조회 |
  | PATCH | `/stores/{storeId}` | 매장 정보 수정 |
  | DELETE | `/stores/{storeId}` | 매장 삭제 |

  2️⃣ 매장 조회 API

  | Method | Endpoint | 설명 |
            | --- | --- | --- |
  | GET | `/stores&page=0&size=30&sortDirection=DESC` | 전체 매장 조회 |
  | GET | `/stores?category={category}&page=0&size=30&sortDirection=DESC` | 카테고리별 매장 조회 |
  | GET | `/stores?sigungu={sigungu}&page=0&size=30&sortDirection=DESC` | 지역(시군구)별 매장 조회 |

  | Method | Endpoint | 설명 |
            | --- | --- | --- |
  | POST | `/category` | 카테고리 생성 |
  | GET | `/category` | 카테고리 조회 |
  | PATCH | `/category/{categoryId}` | 카테고리 수정 |
  | DELETE | `/category/{categoryId}` | 카테고리 삭제 |

  ➡️ **카테고리 및 지역 기준 필터 조회 지원**

<br>
<br>

- 유저, 토큰 API

  | Method | Endpoint | 설명 |
        | --- | --- | --- |
  | POST | /users/signup | 회원가입 |
  | POST | /users/login | 로그인 |
  | POST | /users/logout | 로그아웃 |
  | PATCH | /users/profile/edit | 유저 정보 수정 |
  | DELETE | /users/withdraw | 유저 탈퇴 |
  | DELETE | /users/withdraw/{username} | 유저 탈퇴(관리자) |
  | GET | /users&page=0&size=10&sortDirection=DESC | 전체 유저 조회(관리자) |
  | GET | users/{username}/profile | 유저 프로필 조회(관리자) |
  | GET | /users/profile | 유저 프로필 조회 |

<br>
<br>

- 주문 API

| Method | EndPoint                  | 접근권한               | 기능               | 설명                                                            |
|--------|---------------------------|--------------------|------------------|---------------------------------------------------------------|
| POST   | /orders                   | 고객, 관리자 (OWNER 제외) | 주문생성             | 신규 주문 등록                                                      |
| GET    | /orders/{orderId}         | 인증된 모든 사용자         | 주문 상세 조회         | 특정 주문의 상세 정보를 조회 (본인 확인 로직 포함)                                |
| GET    | /orders/slice             | 인증된 모든 사용자         | 주문 내역 검색 (Slice) | 무한 스크롤 방식(Slice)으로 주문 내역을 검색합니다.(size = 10, 30, 50 생성일자 순 고정) |
| GET    | /orders/page              | MANAGER,MASTER     | 전체 주문 조회 (Page)  | 관리자용 페이징 방식의 전체 주문 내역을 조회합니다.(size = 10, 30, 50 생성일자 순 고정)    |
| PATCH  | /orders/{orderId}/status  | 인증된 모든 사용자         | 주문 상태 변경         | 주문 상태(접수 완료, 배달 중 등)를 업데이트합니다.                                |
| PATCH  | /orders/{orderId}/address | 고객, 관리자 (OWNER 불가) | 주문 주소 변경         | 배달 주소를 수정합니다.                                                 |
| DELETE | /orders/{orderId}         | MANAGER,MASTER     | 주문 삭제 (취소)       | 주문을 소프트 삭제(Soft Delete) 처리합니다.                                |

<br>
<br>

- 상품 API

| Method | EndPoint                   | Description    |
|--------|----------------------------|----------------|
| POST   | /products                  | 상품 생성 API      |
| PATCH  | /products/{productId}      | 상품 수정 API      |
| GET    | /products                  | 상품 전체 조회 API   |
| GET    | /stores/{storeId}/products | 매장 별 상품 조회 API |
| GET    | /products/{productId}      | 상품 상세 조회 API   |
| DELETE | /products/{productId}      | 상품 삭제 API      |

<br>
<br>

- 결제 API

| Method | Endpoint               | 설명           |
|--------|------------------------|--------------|
| POST   | /payment               | 결제 생성 API    |
| GET    | /payment               | 결제 전체 조회 API |
| GET    | /payment?page=0&size=3 | 결제 부분 조희 API |
| GET    | /payment/{paymentId}   | 결제 단일 조회 API |
| PATCH  | /payment/{paymentId}   | 결제 상태 변경 API |
| DELETE | /payment/{paymentId}   | 결제 삭제 API    |

<br>
<br>

- 리뷰 API

| Method | EndPoint                   | 기능        | 설명                                 |
|--------|----------------------------|-----------|------------------------------------|
| POST   | /reviews/                  | 리뷰 작성     | 새로운 리뷰를 등록합니다.                     |
| GET    | /reviews/{reviewId}        | 리뷰 상세 조회  | 특정 리뷰의 상세 정보를 조회합니다.               |
| GET    | /reviews/reviews/{storeId} | 매장별 리뷰 조회 | 특정 매장에 작성된 리뷰 목록을 Slice 형태로 조회합니다. |
| GET    | /reviews/reviews           | 리뷰 전체 조회  | 시스템의 모든 리뷰를 Page 형태로 조회합니다.        |
| PATCH  | /reviews/{reviewId}        | 리뷰 수정     | 작성한 리뷰의 내용을 수정합니다. (작성자 확인 필요)     |
| DELETE | /reviews/{reviewId}        | 리뷰 삭제     | 리뷰를 소프트 삭제(Soft Delete) 처리합니다.     |