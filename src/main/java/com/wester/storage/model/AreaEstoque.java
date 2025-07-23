package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "area_estoque", uniqueConstraints = {
    @UniqueConstraint(columnNames = "nome")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, unique = true)
    private String nome;

    @Lob // Use Lob for potentially large text fields
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Relacionamentos (ex: @OneToMany com Fileira) serão adicionados posteriormente
    // para evitar dependências circulares iniciais e focar na entidade.

}

