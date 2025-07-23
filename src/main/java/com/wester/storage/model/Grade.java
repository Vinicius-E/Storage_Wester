package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "grade", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"fileira_id", "identificador"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileira_id", nullable = false)
    private Fileira fileira;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String identificador;

    @Column(name = "ordem")
    private Integer ordem;

    // Relacionamento inverso (ex: @OneToMany com Nivel) será adicionado depois.
}

