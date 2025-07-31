package com.wester.storage.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank
    private String login;

    @NotBlank
    private String nome;

    private String senha; // senha pura opcional

    private String perfil; // pode ser nulo
}
