package com.wester.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_movimentacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // Automatically set timestamp on creation
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tipo_operacao", nullable = false)
    private String tipoOperacao; // Ex: 'ENTRADA', 'SAIDA', 'MOVIMENTACAO', 'AJUSTE_QUANTIDADE', etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_estoque_id")
    private ItemEstoque itemEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_id")
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_origem_id")
    private Nivel nivelOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_destino_id")
    private Nivel nivelDestino;

    @Column(name = "quantidade_alterada")
    private Integer quantidadeAlterada;

    @Column(name = "quantidade_anterior")
    private Integer quantidadeAnterior;

    @Column(name = "quantidade_nova")
    private Integer quantidadeNova;

    @Lob
    @Column(name = "detalhes_alteracao", columnDefinition = "TEXT")
    private String detalhesAlteracao; // Can store JSON or descriptive text

}

