package com.firstcomestore.domain.order.entity;

/**
 * ORDER_COMPLETE = 주문 완료 / ON_DELIVERY = 배송중(1일 후) / DELIVERY_COMPLETE = 배송 완료(2일 후) / ON_RETURN =
 * 반품 신청(1일 이내 가능) / RETURN_COMPLETE = 반품 완료
 */
public enum OrderStatus {
    ORDER_COMPLETE,
    ON_DELIVERY,
    DELIVERY_COMPLETE,
    CANCEL,
    ON_RETURN,
    RETURN_COMPLETE
}
