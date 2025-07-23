package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "produto", uniqueConstraints = {
    @UniqueConstraint(columnNames = "codigo_sistema_wester")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "codigo_sistema_wester", nullable = false, unique = true)
    private String codigoSistemaWester;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nome_modelo", nullable = false)
    private String nomeModelo;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String cor;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Relacionamento inverso (ex: @OneToMany com ItemEstoque) será adicionado depois.
}

