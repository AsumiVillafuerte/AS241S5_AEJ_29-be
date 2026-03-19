package pe.edu.vallegrande.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "product")
public class Product {

    @Id
    @Column(value = "id_product")
    private Long idProduct;

    @Column(value = "name")
    private String name;

    @Column(value = "description")
    private String description;

    @Column(value = "price")
    private BigDecimal price;

    @Column(value = "stock")
    private Integer stock;

    @Column(value = "category")
    private String category;

    @Column(value = "created_at")
    private LocalDateTime createdAt;

    @Column(value = "status")
    private String status;

}
