package com.kata.cubbyhole.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "plans")
public class Plan extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String name;

    @NotNull
    private Double price;

    @NotNull
    private Long duration;

    @NotNull
    private Long storagespace;

    public Plan() {
        // Default Constructor
    }

    public Plan(@NotBlank @Size(max = 40) String name, @NotNull Double price, @NotNull Long duration, @NotNull Long storagespace) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.storagespace = storagespace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getStoragespace() {
        return storagespace;
    }

    public void setStoragespace(Long storagespace) {
        this.storagespace = storagespace;
    }
}
