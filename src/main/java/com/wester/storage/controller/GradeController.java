package com.wester.storage.controller;

import com.wester.storage.model.Grade;
import com.wester.storage.service.GradeService;
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

@Tag(name = "Grade", description = "CRUD for Grade")
@RestController
@RequestMapping("/api/grades") // Base path for grade endpoints
public class GradeController {

    @Autowired
    private GradeService gradeService;

    // Endpoint to list grades within a specific fileira
    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/fileira/{fileiraId}")
    public ResponseEntity<List<Grade>> listarGradesPorFileira(@PathVariable Long fileiraId) {
        List<Grade> grades = gradeService.listarGradesPorFileira(fileiraId);
        return ResponseEntity.ok(grades);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<Grade> buscarGradePorId(@PathVariable Long id) {
        return gradeService.buscarGradePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to create a grade within a specific fileira
    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping("/fileira/{fileiraId}")
    public ResponseEntity<?> criarGrade(@PathVariable Long fileiraId, @Valid @RequestBody Grade grade) {
        // Add security check: Only ADMIN?
        try {
            Grade novaGrade = gradeService.salvarGrade(grade, fileiraId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) { // Catch Fileira not found
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar Grade.");
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao criar Grade.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarGrade(@PathVariable Long id, @Valid @RequestBody Grade grade) {
         // Add security check: Only ADMIN?
         // Note: The service prevents changing the Fileira ID.
         // The request body should ideally contain the Fileira ID to match the existing one or be omitted.
         try {
            Grade gradeAtualizada = gradeService.atualizarGrade(id, grade);
            return ResponseEntity.ok(gradeAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar Grade.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar Grade.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarGrade(@PathVariable Long id) {
        // Add security check: Only ADMIN?
        try {
            gradeService.deletarGrade(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Catch dependency error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrada")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Grade.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar Grade.");
        }
    }
}

