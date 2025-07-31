package com.wester.storage.mapper;

import com.wester.storage.dto.UsuarioResponseDTO;
import com.wester.storage.model.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDTO toDTO(Usuario usuario, String token) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfil(),
                token
        );
    }

    public static UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfil(),
                null
        );
    }
}
