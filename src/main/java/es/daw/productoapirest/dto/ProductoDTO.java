package es.daw.productoapirest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @DecimalMin(value = "100.00", message = "El precio debe superior a 99") // no vale solo Min porque no es un int, es un decimal y ello necesita que se ponga entrecomillas
    private BigDecimal precio;
    @Size(min = 4, max = 4, message = "El SKU debe tener exactamente 4 caracteres")
    private String codigo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //solo se usa esta propiedad al crear un producto no se devuelve al listar
    private Integer codigoFabricante;
}
