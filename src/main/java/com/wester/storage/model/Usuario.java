package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = "login")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank
    @Size(max = 255) // Adjust size based on hashing algorithm output length
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    // Consider using an Enum for profile if possible in the future
    // @Enumerated(EnumType.STRING)
    private String perfil; // Potential values: "LEITURA", "ADMINISTRADOR"

    // Relacionamento inverso (ex: @OneToMany com HistoricoMovimentacao) será adicionado depois.
}

