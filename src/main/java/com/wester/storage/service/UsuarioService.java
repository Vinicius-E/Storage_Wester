package com.wester.storage.service;

import com.wester.storage.config.JwtSecurityConfig;
import com.wester.storage.dto.LoginDTO;
import com.wester.storage.dto.UsuarioResponseDTO;
import com.wester.storage.model.Usuario;
import com.wester.storage.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private JwtSecurityConfig jwtSecurityConfig;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }


    public UsuarioResponseDTO autenticar(LoginDTO dto) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin"));
        System.out.println("🔐 Iniciando autenticação para login: " + dto.getLogin());

        return buscarPorLogin(dto.getLogin())
                .map(usuario -> {
                    System.out.println("👤 Usuário encontrado: " + usuario.getLogin());
                    boolean senhaValida = passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash());

                    if (!senhaValida) {
                        return new UsuarioResponseDTO(
                                null,
                                dto.getLogin(),
                                "Usuário ou senha inválido",
                                "INVALIDO",
                                null
                        );
                    }

                    String token = jwtSecurityConfig.generateToken(usuario.getLogin());
                    System.out.println("✅ Autenticação bem-sucedida. Token gerado.");

                    return new UsuarioResponseDTO(
                            usuario.getId(),
                            usuario.getLogin(),
                            usuario.getNome(),
                            usuario.getPerfil(),
                            token
                    );
                })
                .orElseGet(() -> {
                    System.out.println("❌ Usuário não encontrado: " + dto.getLogin());
                    return new UsuarioResponseDTO(
                            null,
                            dto.getLogin(),
                            "Usuário ou senha inválido",
                            "INVALIDO",
                            null
                    );
                });
    }


    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        // Atribuir perfil padrão caso não tenha sido informado
        if (usuario.getPerfil() == null || usuario.getPerfil().isBlank()) {
            usuario.setPerfil("LEITURA");
        }

        // Validar perfil
        if (!isValidProfile(usuario.getPerfil())) {
            throw new IllegalArgumentException("Perfil inválido: " + usuario.getPerfil() + ". Use 'LEITURA' ou 'ADMINISTRADOR'.");
        }

        // Verificar duplicação de login
        if (usuario.getId() == null && buscarPorLogin(usuario.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Login já existe: " + usuario.getLogin());
        }

        return usuarioRepository.save(usuario);
    }


    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Validate profile
            if (!isValidProfile(usuarioAtualizado.getPerfil())) {
                throw new IllegalArgumentException("Perfil inválido: " + usuarioAtualizado.getPerfil() + ". Use 'LEITURA' ou 'ADMINISTRADOR'.");
            }
            // Check if the updated login conflicts with another existing user
            Optional<Usuario> userWithSameLogin = buscarPorLogin(usuarioAtualizado.getLogin());
            if (!usuarioExistente.getLogin().equals(usuarioAtualizado.getLogin()) &&
                    userWithSameLogin.isPresent() && !userWithSameLogin.get().getId().equals(id)) {
                throw new IllegalArgumentException("Login já existe: " + usuarioAtualizado.getLogin());
            }

            usuarioExistente.setNome(usuarioAtualizado.getNome());
            usuarioExistente.setLogin(usuarioAtualizado.getLogin());
            usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

            // Handle password update separately - typically requires current password or admin privilege
            // If usuarioAtualizado.getSenhaHash() is not null or empty, update it after encoding
            // if (usuarioAtualizado.getSenhaHash() != null && !usuarioAtualizado.getSenhaHash().isEmpty()) {
            //     String encodedPassword = passwordEncoder.encode(usuarioAtualizado.getSenhaHash());
            //     usuarioExistente.setSenhaHash(encodedPassword);
            // }

            return usuarioRepository.save(usuarioExistente);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com id: " + id);
        }
        // Add checks if user has dependencies (e.g., created history records) if needed
        usuarioRepository.deleteById(id);
    }

    private boolean isValidProfile(String perfil) {
        return "LEITURA".equals(perfil) || "ADMINISTRADOR".equals(perfil);
    }

    // Method for password change (requires more logic, like checking old password)
    // public void changePassword(Long userId, String oldPassword, String newPassword) { ... }
}

