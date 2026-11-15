package com.learning.caching.entity;


import jakarta.persistence.*;
        import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a product stored in the products table.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    /**
     * Creates an empty product entity.
     */
    public Product() {
    }

    /**
     * Sets the creation timestamp immediately before the entity is first persisted.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Returns the product's database identifier.
     *
     * @return the product identifier, or {@code null} before persistence
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the product's database identifier.
     *
     * @param id the product identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the product name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the product description.
     *
     * @return the product description, or {@code null} if none is set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     *
     * @param description the product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the product price.
     *
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the product price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Sets the product creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the product creation timestamp.
     *
     * @return the creation timestamp, or {@code null} before it is set
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
