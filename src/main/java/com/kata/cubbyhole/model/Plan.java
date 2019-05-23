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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plan plan = (Plan) o;

        if (!id.equals(plan.id)) return false;
        if (!name.equals(plan.name)) return false;
        if (!price.equals(plan.price)) return false;
        if (!duration.equals(plan.duration)) return false;
        return storagespace.equals(plan.storagespace);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + storagespace.hashCode();
        return result;
    }
}
