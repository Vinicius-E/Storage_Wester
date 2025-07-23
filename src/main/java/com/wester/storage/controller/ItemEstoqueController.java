package com.wester.storage.controller;

import com.wester.storage.dto.ItemEstoqueDTO;
import com.wester.storage.dto.MovimentacaoRequestDTO;
import com.wester.storage.model.ItemEstoque;
import com.wester.storage.service.ItemEstoqueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "ItemEstoque", description = "CRUD for ItemEstoque")
@RestController
@RequestMapping("/api/itens-estoque") // Base path for stock item endpoints
public class ItemEstoqueController {

    @Autowired
    private ItemEstoqueService itemEstoqueService;

    // Endpoint to list items within a specific nivel
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/nivel/{nivelId}")
    public ResponseEntity<List<ItemEstoqueDTO>> listarItensPorNivel(@PathVariable Long nivelId) {
        List<ItemEstoque> itens = itemEstoqueService.listarItensPorNivel(nivelId);
        List<ItemEstoqueDTO> dtos = itens.stream().map(ItemEstoqueDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Endpoint to list items for a specific product
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<ItemEstoqueDTO>> listarItensPorProduto(@PathVariable Long produtoId) {
        List<ItemEstoque> itens = itemEstoqueService.listarItensPorProduto(produtoId);
        List<ItemEstoqueDTO> dtos = itens.stream().map(ItemEstoqueDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Endpoint to get a specific item by nivel and produto
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/localizar")
    public ResponseEntity<ItemEstoqueDTO> buscarItemPorNivelEProduto(
            @RequestParam Long nivelId,
            @RequestParam Long produtoId) {
        return itemEstoqueService.buscarItemPorNivelEProduto(nivelId, produtoId)
                .map(item -> ResponseEntity.ok(ItemEstoqueDTO.fromEntity(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to add quantity to an item (or create if not exists)
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarQuantidadeItem(@Valid @RequestBody MovimentacaoRequestDTO request) {
        // Add security check: Only ADMIN?
        Long usuarioId = 1L; // Placeholder
        try {
            ItemEstoque itemAtualizado = itemEstoqueService.adicionarOuAtualizarItem(
                    request.getNivelId(),
                    request.getProdutoId(),
                    request.getQuantidade(),
                    usuarioId
            );
            return ResponseEntity.ok(ItemEstoqueDTO.fromEntity(itemAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar item ao estoque.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao adicionar item.");
        }
    }

    // Endpoint to remove quantity from an item
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/remover")
    public ResponseEntity<?> removerQuantidadeItem(@Valid @RequestBody MovimentacaoRequestDTO request) {
        // Add security check: Only ADMIN?
        Long usuarioId = 1L; // Placeholder
        try {
            itemEstoqueService.removerQuantidadeItem(
                    request.getNivelId(),
                    request.getProdutoId(),
                    request.getQuantidade(),
                    usuarioId
            );
            return ResponseEntity.ok().body("Quantidade removida com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover item do estoque.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao remover item.");
        }
    }

    // Endpoint to move quantity between niveis
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/mover")
    public ResponseEntity<?> moverItem(@Valid @RequestBody MovimentacaoRequestDTO request) {
        // Add security check: Only ADMIN?
        Long usuarioId = 1L; // Placeholder
        if (request.getNivelOrigemId() == null || request.getNivelDestinoId() == null) {
            return ResponseEntity.badRequest().body("Nível de origem e destino são obrigatórios para movimentação.");
        }
        try {
            itemEstoqueService.moverItem(
                    request.getNivelOrigemId(),
                    request.getNivelDestinoId(),
                    request.getProdutoId(),
                    request.getQuantidade(),
                    usuarioId
            );
            return ResponseEntity.ok().body("Item movido com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             if (e.getMessage().contains("revertida")) {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
             }
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao mover item.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao mover item.");
        }
    }

    // *** NEW ENDPOINT FOR SEARCHING ITEMS ***
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/pesquisar")
    public ResponseEntity<List<ItemEstoqueDTO>> pesquisarItens(
            @RequestParam(required = false) String nomeModelo,
            @RequestParam(required = false) String codigoWester,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) Integer quantidadeMin,
            @RequestParam(required = false) Integer quantidadeMax,
            @RequestParam(required = false) Long areaId) {
        try {
            List<ItemEstoque> itens = itemEstoqueService.pesquisarItens(
                    nomeModelo, codigoWester, cor, quantidadeMin, quantidadeMax, areaId
            );
            List<ItemEstoqueDTO> dtos = itens.stream().map(ItemEstoqueDTO::fromEntity).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Note: DTOs (ItemEstoqueDTO, MovimentacaoRequestDTO) need to be created.
}

