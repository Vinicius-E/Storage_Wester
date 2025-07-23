package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "nivel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"grade_id", "identificador"}),
    @UniqueConstraint(columnNames = {"identificador_completo_legivel"}) // If this field is used and must be unique
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String identificador;

    @Column(name = "ordem")
    private Integer ordem;

    @Size(max = 255)
    @Column(name = "identificador_completo_legivel", unique = true) // Mapped as unique based on model
    private String identificadorCompletoLegivel;

    // Relacionamento inverso (ex: @OneToMany com ItemEstoque) será adicionado depois.
}

