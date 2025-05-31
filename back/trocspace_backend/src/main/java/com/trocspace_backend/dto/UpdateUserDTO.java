package com.trocspace_backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;
}
