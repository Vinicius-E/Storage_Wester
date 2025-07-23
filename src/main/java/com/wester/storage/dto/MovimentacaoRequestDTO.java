package com.wester.storage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimentacaoRequestDTO {

    // Used for adding/removing quantity
    private Long nivelId;

    // Used for moving quantity
    private Long nivelOrigemId;
    private Long nivelDestinoId;

    @NotNull(message = "ID do Produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantidade;

}

