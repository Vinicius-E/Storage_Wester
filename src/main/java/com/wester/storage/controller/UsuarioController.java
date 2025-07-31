package com.wester.storage.controller;

import com.wester.storage.config.JwtSecurityConfig;
import com.wester.storage.dto.LoginDTO;
import com.wester.storage.dto.UsuarioRequestDTO;
import com.wester.storage.dto.UsuarioResponseDTO;
import com.wester.storage.mapper.UsuarioMapper;
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
import java.util.Map;
import java.util.Optional;

import com.wester.storage.model.Usuario;
import com.wester.storage.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@CrossOrigin(origins = "http://localhost:8081")  // ou defina especificamente seu frontend, ex: "http://localhost:8081"
@Tag(name = "Usuario", description = "CRUD for Usuario")
@RestController
@RequestMapping("/api/usuarios") // Base path for user endpoints
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtSecurityConfig jwtSecurityConfig;


    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios() {
        List<UsuarioResponseDTO> dtos = usuarioService.listarTodosUsuarios()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(UsuarioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get resource(s)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/login/{login}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorLogin(@PathVariable String login) {
        return usuarioService.buscarPorLogin(login)
                .map(UsuarioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create resource")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        try {
            if (dto.getSenha() == null || dto.getSenha().isBlank()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória.");
            }

            Usuario usuario = new Usuario();
            usuario.setLogin(dto.getLogin());
            usuario.setNome(dto.getNome());
            usuario.setPerfil(dto.getPerfil() != null ? dto.getPerfil() : "LEITURA");
            usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));

            Usuario salvo = usuarioService.salvarUsuario(usuario);
            UsuarioResponseDTO responseDTO = UsuarioMapper.toDTO(salvo);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar usuário.");
        }
    }


    @Operation(summary = "Update resource")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated")})
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
            UsuarioResponseDTO dto = UsuarioMapper.toDTO(usuarioAtualizado);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar usuário.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar usuário.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        try {
            UsuarioResponseDTO response = usuarioService.autenticar(dto);
            if ("INVALIDO".equalsIgnoreCase(response.getPerfil())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UsuarioResponseDTO(null, dto.getLogin(), "Erro interno", "ERRO", null));
        }
    }

//    @PostMapping("/login")
//    @Operation(summary = "Login")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
//            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
//            @ApiResponse(responseCode = "500", description = "Erro interno")
//    })
//    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
//        try {
//            return usuarioService.buscarPorLogin(dto.getLogin())
//                    .filter(usuario -> passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash()))
//                    .map(usuario -> ResponseEntity.ok(UsuarioMapper.toDTO(usuario)))
//                    .orElseGet(() -> ResponseEntity
//                            .status(HttpStatus.UNAUTHORIZED)
//                            .body(new UsuarioResponseDTO(null, dto.getLogin(), "Usuário ou senha inválido", "INVALIDO")));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new UsuarioResponseDTO(null, dto.getLogin(), "Erro interno", "ERRO"));
//        }
//    }
}

