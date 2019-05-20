package com.kata.cubbyhole.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Instant expireOn;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    public Subscription(@NotNull Instant expireOn, @NotBlank User user, @NotBlank Plan plan) {
        this.expireOn = expireOn;
        this.user = user;
        this.plan = plan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(Instant expireOn) {
        this.expireOn = expireOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
