package com.wester.storage.controller;

import com.wester.storage.model.Produto;
import com.wester.storage.service.ProdutoService;
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

@Tag(name = "Produto", description = "CRUD for Produto")
@RestController
@RequestMapping("/api/produtos") // Define base path for product endpoints
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosProdutos() {
        List<Produto> produtos = produtoService.listarTodosProdutos();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        return produtoService.buscarProdutoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/codigo/{codigoWester}")
    public ResponseEntity<Produto> buscarPorCodigoWester(@PathVariable String codigoWester) {
        return produtoService.buscarPorCodigoWester(codigoWester)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.salvarProduto(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar produto.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody Produto produto) {
         try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) { // Catch specific exception for not found
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar produto.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar produto.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Catch specific exception for not found
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Avoid sending body on error for DELETE
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Add endpoints for search if needed, e.g.:
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarProdutos(
            @RequestParam(required = false) String nomeModelo,
            @RequestParam(required = false) String cor) {
        // Implement logic in service to handle different combinations of parameters
        if (nomeModelo != null) {
             return ResponseEntity.ok(produtoService.buscarPorNomeModeloContendo(nomeModelo));
        }
        if (cor != null) {
             return ResponseEntity.ok(produtoService.buscarPorCor(cor));
        }
        // Default to returning all if no specific search param provided, or handle error
        return ResponseEntity.ok(produtoService.listarTodosProdutos());
    }

}

