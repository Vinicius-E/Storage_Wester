package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "fileira", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"area_estoque_id", "identificador"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fileira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching is generally recommended
    @JoinColumn(name = "area_estoque_id", nullable = false)
    private AreaEstoque areaEstoque;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String identificador;

    @Column(name = "ordem") // Explicitly map column name if different from field name
    private Integer ordem;

    // Relacionamento inverso (ex: @OneToMany com Grade) será adicionado depois.
}

