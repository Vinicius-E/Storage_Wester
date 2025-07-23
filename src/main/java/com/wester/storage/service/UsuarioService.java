package com.wester.storage.service;

import com.wester.storage.model.Usuario;
import com.wester.storage.repository.UsuarioRepository;
// Import a password encoder, e.g., from Spring Security
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Inject PasswordEncoder (configure a Bean for it elsewhere, e.g., BCryptPasswordEncoder)
    // @Autowired
    // private PasswordEncoder passwordEncoder;

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

    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        // Validate profile
        if (!isValidProfile(usuario.getPerfil())) {
             throw new IllegalArgumentException("Perfil inválido: " + usuario.getPerfil() + ". Use 'LEITURA' ou 'ADMINISTRADOR'.");
        }
        // Check if login already exists for new users
        if (usuario.getId() == null && buscarPorLogin(usuario.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Login já existe: " + usuario.getLogin());
        }
        // Encode password before saving - IMPORTANT: Implement password encoding!
        // String encodedPassword = passwordEncoder.encode(usuario.getSenhaHash()); // Assuming raw password is sent initially
        // usuario.setSenhaHash(encodedPassword);
        // For now, saving the hash as is, assuming it's already hashed by the controller/DTO layer or for simplicity
        // In a real app, ALWAYS hash passwords securely.
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

