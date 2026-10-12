package com.pokeprint.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerReviewSubmitDTO(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    String customerName,

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1 estrela")
    @Max(value = 5, message = "A nota máxima é 5 estrelas")
    Integer rating,

    @NotBlank(message = "O depoimento é obrigatório")
    @Size(max = 500, message = "O depoimento não pode exceder 500 caracteres")
    String comment,

    @NotBlank(message = "A identificação da peça adquirida é obrigatória")
    @Size(max = 150, message = "O nome da peça não pode exceder 150 caracteres")
    String productName,

    @Size(max = 100, message = "A cidade não pode exceder 100 caracteres")
    String customerCity
) {}
