package com.wester.storage.controller;

import com.wester.storage.model.Usuario;
import com.wester.storage.service.UsuarioService;
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

@Tag(name = "Usuario", description = "CRUD for Usuario")
@RestController
@RequestMapping("/api/usuarios") // Base path for user endpoints
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        // Add security check here: Only ADMIN should list all users
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        // Consider returning a DTO list without password hashes
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        // Add security check: User can see their own profile, ADMIN can see any
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok) // Consider returning DTO without hash
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/login/{login}")
    public ResponseEntity<Usuario> buscarPorLogin(@PathVariable String login) {
         // Add security check: User can see their own profile, ADMIN can see any
        return usuarioService.buscarPorLogin(login)
                .map(ResponseEntity::ok) // Consider returning DTO without hash
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody Usuario usuario) {
        // Add security check: Only ADMIN should create users
        // Consider using a DTO for input to separate password field
        try {
            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
            // Return DTO without hash
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar usuário.");
        }
    }

    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        // Add security check: User can update their own profile (limited fields), ADMIN can update any
        // Consider using a DTO for input
         try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
             // Return DTO without hash
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar usuário.");
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar usuário.");
        }
    }

    @Operation(summary = "Delete resource")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
         // Add security check: Only ADMIN should delete users
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
             if (e.getMessage().contains("não encontrado")) {
                 return ResponseEntity.notFound().build();
             }
             // Log the exception e
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
         } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Add endpoints for password change if needed

}

