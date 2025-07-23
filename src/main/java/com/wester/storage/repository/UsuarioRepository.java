package com.wester.storage.repository;

import com.wester.storage.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    // Add other custom queries as needed, e.g., find by profile
    // List<Usuario> findByPerfil(String perfil);
}

