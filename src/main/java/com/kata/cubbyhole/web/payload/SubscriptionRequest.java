package com.kata.cubbyhole.web.payload;

import javax.validation.constraints.NotNull;

public class SubscriptionRequest {

    @NotNull(message = "user cannot be empty")
    private Long userId;
    @NotNull(message = "plan cannot be empty")
    private Long planId;

    public SubscriptionRequest() {
        // Default Constructor
    }

    public SubscriptionRequest(@NotNull Long userId, @NotNull Long planId) {
        this.userId = userId;
        this.planId = planId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
