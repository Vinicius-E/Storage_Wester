package com.wester.storage.controller;

import com.wester.storage.model.Fileira;
import com.wester.storage.service.FileiraService;
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

@Tag(name = "Fileira", description = "CRUD for Fileira")
@RestController
@RequestMapping("/api/fileiras") // Base path for fileira endpoints
public class FileiraController {

    @Autowired
    private FileiraService fileiraService;

    // Endpoint to list fileiras within a specific area
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<Fileira>> listarFileirasPorArea(@PathVariable Long areaId) {
        List<Fileira> fileiras = fileiraService.listarFileirasPorArea(areaId);
        return ResponseEntity.ok(fileiras);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<Fileira> buscarFileiraPorId(@PathVariable Long id) {
        return fileiraService.buscarFileiraPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to create a fileira within a specific area
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/area/{areaId}")
    public ResponseEntity<?> criarFileira(@PathVariable Long areaId, @Valid @RequestBody Fileira fileira) {
        // Add security check: Only ADMIN?
        try {
            Fileira novaFileira = fileiraService.salvarFileira(fileira, areaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaFileira);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) { // Catch Area not found
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar Fileira.");
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar Fileira.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFileira(@PathVariable Long id, @Valid @RequestBody Fileira fileira) {
         // Add security check: Only ADMIN?
         // Note: The service prevents changing the AreaEstoque ID.
         // The request body should ideally contain the AreaEstoque ID to match the existing one or be omitted.
         try {
            Fileira fileiraAtualizada = fileiraService.atualizarFileira(id, fileira);
            return ResponseEntity.ok(fileiraAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar Fileira.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar Fileira.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFileira(@PathVariable Long id) {
        // Add security check: Only ADMIN?
        try {
            fileiraService.deletarFileira(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Catch dependency error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Fileira.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar Fileira.");
        }
    }
}

