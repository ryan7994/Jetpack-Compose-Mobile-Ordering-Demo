package com.ryanjames.composemobileordering.domain


enum class OrderStatus {
    CREATED, CANCELLED, CHECKOUT, UNKNOWN, PREPARING, DELIVERING, READY_FOR_PICKUP, DELIVERED, PICKED_UP
}