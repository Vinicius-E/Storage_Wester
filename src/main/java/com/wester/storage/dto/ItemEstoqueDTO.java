package com.wester.storage.dto;

import com.wester.storage.model.ItemEstoque;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEstoqueDTO {

    private Long id;
    private Long nivelId;
    private String nivelIdentificador; // Example: N1
    private String nivelCompletoIdentificador; // Example: Area1-F1-G1-N1
    private Long produtoId;
    private String produtoCodigoWester;
    private String produtoNomeModelo;
    private String produtoCor;
    private Integer quantidade;
    private LocalDateTime dataAtualizacao;

    // Static factory method to convert Entity to DTO
    public static ItemEstoqueDTO fromEntity(ItemEstoque item) {
        if (item == null) {
            return null;
        }
        return new ItemEstoqueDTO(
                item.getId(),
                item.getNivel() != null ? item.getNivel().getId() : null,
                item.getNivel() != null ? item.getNivel().getIdentificador() : null,
                item.getNivel() != null ? item.getNivel().getIdentificadorCompletoLegivel() : null, // Assuming this is populated
                item.getProduto() != null ? item.getProduto().getId() : null,
                item.getProduto() != null ? item.getProduto().getCodigoSistemaWester() : null,
                item.getProduto() != null ? item.getProduto().getNomeModelo() : null,
                item.getProduto() != null ? item.getProduto().getCor() : null,
                item.getQuantidade(),
                item.getDataAtualizacao()
        );
    }
}

