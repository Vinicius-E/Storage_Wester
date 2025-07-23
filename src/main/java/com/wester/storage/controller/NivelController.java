package com.wester.storage.controller;

import com.wester.storage.model.Nivel;
import com.wester.storage.service.NivelService;
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

@Tag(name = "Nivel", description = "CRUD for Nivel")
@RestController
@RequestMapping("/api/niveis") // Base path for nivel endpoints
public class NivelController {

    @Autowired
    private NivelService nivelService;

    // Endpoint to list niveis within a specific grade
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/grade/{gradeId}")
    public ResponseEntity<List<Nivel>> listarNiveisPorGrade(@PathVariable Long gradeId) {
        List<Nivel> niveis = nivelService.listarNiveisPorGrade(gradeId);
        return ResponseEntity.ok(niveis);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<Nivel> buscarNivelPorId(@PathVariable Long id) {
        return nivelService.buscarNivelPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to create a nivel within a specific grade
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/grade/{gradeId}")
    public ResponseEntity<?> criarNivel(@PathVariable Long gradeId, @Valid @RequestBody Nivel nivel) {
        // Add security check: Only ADMIN?
        try {
            Nivel novoNivel = nivelService.salvarNivel(nivel, gradeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoNivel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) { // Catch Grade not found
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar Nível.");
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar Nível.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarNivel(@PathVariable Long id, @Valid @RequestBody Nivel nivel) {
         // Add security check: Only ADMIN?
         // Note: The service prevents changing the Grade ID.
         // The request body should ideally contain the Grade ID to match the existing one or be omitted.
         try {
            Nivel nivelAtualizado = nivelService.atualizarNivel(id, nivel);
            return ResponseEntity.ok(nivelAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar Nível.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar Nível.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarNivel(@PathVariable Long id) {
        // Add security check: Only ADMIN?
        try {
            nivelService.deletarNivel(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Catch dependency error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Nível.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar Nível.");
        }
    }
}

