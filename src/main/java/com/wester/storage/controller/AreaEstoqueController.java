package com.wester.storage.controller;

import com.wester.storage.model.AreaEstoque;
import com.wester.storage.service.AreaEstoqueService;
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

@Tag(name = "AreaEstoque", description = "CRUD for AreaEstoque")
@RestController
@RequestMapping("/api/areas") // Base path for area endpoints
public class AreaEstoqueController {

    @Autowired
    private AreaEstoqueService areaEstoqueService;



    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping
    public ResponseEntity<List<AreaEstoque>> listarTodasAreas() {
        List<AreaEstoque> areas = areaEstoqueService.listarTodasAreas();
        return ResponseEntity.ok(areas);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<AreaEstoque> buscarAreaPorId(@PathVariable Long id) {
        return areaEstoqueService.buscarAreaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/nome/{nome}")
    public ResponseEntity<AreaEstoque> buscarPorNome(@PathVariable String nome) {
        return areaEstoqueService.buscarPorNome(nome)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<?> criarArea(@Valid @RequestBody AreaEstoque areaEstoque) {
        // Add security check: Only ADMIN?
        try {
            AreaEstoque novaArea = areaEstoqueService.salvarArea(areaEstoque);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar Área de Estoque.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarArea(@PathVariable Long id, @Valid @RequestBody AreaEstoque areaEstoque) {
        // Add security check: Only ADMIN?
         try {
            AreaEstoque areaAtualizada = areaEstoqueService.atualizarArea(id, areaEstoque);
            return ResponseEntity.ok(areaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar Área de Estoque.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar Área de Estoque.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarArea(@PathVariable Long id) {
        // Add security check: Only ADMIN?
        try {
            areaEstoqueService.deletarArea(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Catch dependency error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Área de Estoque.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar Área de Estoque.");
        }
    }
}

