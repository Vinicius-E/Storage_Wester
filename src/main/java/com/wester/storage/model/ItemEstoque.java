package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_estoque", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nivel_id", "produto_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_id", nullable = false)
    private Nivel nivel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Min(0) // Quantity cannot be negative
    @Column(nullable = false)
    private Integer quantidade = 0; // Default to 0

    @UpdateTimestamp // Automatically update timestamp on modification
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

}

