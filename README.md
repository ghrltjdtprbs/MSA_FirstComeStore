# 📌 FirstComeStore_MSA
- E-commerce 프로젝트입니다.
- MSA 를 적용하였습니다.
- **⭐️프로젝트의 편리한 관리를 위해 각 micro service는 branch별로 관리합니다.⭐️**
----
# 🛠️ Tech Stack
- **Framework & Library** : 
Spring Boot, Spring WebFlux, Spring Cloud, Java, JPA, Java Mail Sender
- **DevOps** : AWS, EC2, Docker, Netflix Eureka, env(로컬환경 변수 관리), GitHub Secrets
- **Security** : JWT, Spring Security
- **Database** : MySQL, Redis
- **Monitoring** : Spring Actuator, Grafana, Prometheus
----
# 🖼️ Architecture
<img width="748" alt="스크린샷 2024-09-05 19 41 51" src="https://github.com/user-attachments/assets/9a575eff-d9a5-449f-b38f-a5b848da6715">

----
# 🪵 branch
- **main** : 모놀리식 프로젝트
- **api-gateway** : api gateway service
- **discovery-service** : discovery service
- **user-service** : user micro service
- **product-service** : product micro service
- **order-service** : order micro service
----
# 📃 user-service
- 회원 관리에 관련된 서비스를 제공하는 마이크로 서비스입니다.
- 자세한 API 정보 및 테스트 케이스는 [API 문서](https://sapphire-behavior-785.notion.site/FirstComeStore-API-3f6b06ad702848e49937178909f3a885) 참고
- 로그인시 JWT 토큰 생성 및 쿠키에 저장(JWT 토큰 검증/인증은 API Gateway에서 수행)
- SSO(Single Sign-On)패턴을 채택해 한 번 로그인 하면 JWT 기반으로 모든 마이크로서비스에서 로그인된 상태로 요청 처리 가능
### API
- **(인증코드 전송)** POST /user-service/users/email
- **(인증코드 검증)** POST /user-service/users/email/verify
- **(회원가입)** POST /user-service/users
- **(로그인)** POST /user-service/users/login
- **(로그아웃)** POST /user-service/users/logout
- **(회원탈퇴)** DELETE /user-service/users
- **(관리자 회원 가입)** POST /user-service/admin
- **(내 정보 조회)** GET /user-service/mypage
- **(내 정보 수정)** PATCH /user-service/mypage
- **(비밀번호 변경)** PATCH /user-service/mypage/password
### ERD
![user-service](https://github.com/user-attachments/assets/ea2f45b8-9f2a-4950-9054-b1186a956f20)

----
# 📃 product-service
- 상품 관리에 관련된 서비스를 제공하는 마이크로 서비스입니다.
- 자세한 API 정보 및 테스트 케이스는 [API 문서](https://sapphire-behavior-785.notion.site/FirstComeStore-API-3f6b06ad702848e49937178909f3a885) 참고
- order-service와 FeignClient를 통해 통신
### API
- (상품 등록) POST /product-service/admin/products
- (상품 옵션 등록) POST /product-service/admin/products/{productId}/options
- (상품 목록 조회) GET /product-service/products
- (상품 상세 조회) GET /product-service/products/{productId}
- (최대구매수량 설정) PUT /product-service/admin/products/options/{optionId}/max-purchase-limit
### ERD
![product-service](https://github.com/user-attachments/assets/1b501b6f-0e94-45ac-93ff-cf2bf85be1fc)

----
# 📃 order-service
- 주문에 관련된 서비스를 제공하는 마이크로 서비스입니다.
- 자세한 API 정보 및 테스트 케이스는 [API 문서](https://sapphire-behavior-785.notion.site/FirstComeStore-API-3f6b06ad702848e49937178909f3a885) 참고
- product-service와 FeignClient를 통해 통신
- Redis 분산락을 사용해 선착순 주문 동시성 제어 처리(Jmeter로 테스트 완료 [상세보기](https://velog.io/@ghrltjdtprbs/%EC%84%A0%EC%B0%A9%EC%88%9C-%EA%B5%AC%EB%A7%A4-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%ED%85%8C%EC%8A%A4%ED%8A%B8))
- Resilience4j를 활용하여 [서킷브레이커 구현](https://velog.io/@ghrltjdtprbs/Resilience4j%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%84%9C%ED%82%B7-%EB%B8%8C%EB%A0%88%EC%9D%B4%EC%BB%A4-%ED%8C%A8%ED%84%B4-%EA%B5%AC%ED%98%84)
- Spring Actuator로 서버 상태와 서킷브레이커 상태 모니터링
  
### API
- (위시리스트 추가) POST /order-service/wishlist/options/{optionId}
- (위시리스트 조회) GET /order-service/wishlist
- (위시리스트 삭제) DELETE /order-service/wishlist/{wishId}
- (위시리스트 수정) PUT /order-service/wishlist/{wishId}
- (주문) POST /order-service/orders/{wishId}
- (주문 조회) GET /order-service/orders
- (주문 취소) PUT /order-service/orders/{orderId}/cancel
- (반품 신청) PUT /order-service/orders/{orderId}/return
### ERD
![order-service](https://github.com/user-attachments/assets/2e3a2acf-6abb-47d8-86e5-1e87d1845c19)

----
# 📃 API Gateway
- 모든 요청을 받아 discovery sevice에 등록된 마이크로 서비스로 요청 전달 및 반환
- JWT 검증 및 인증 수행
- discovery sevice에 등록
