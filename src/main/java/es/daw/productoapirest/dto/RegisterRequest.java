package es.daw.productoapirest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegisterRequest {

    @NotBlank(message = "El nombre se usuarioes obligatorio")
    private String username;

    @NotBlank(message = "La contrasenia es obligatoria")
    private String password;
    // trabajar en y si queremos indicar varios roles
     private String role; //<-- envia un solo rol

    //private List<String> roles = new ArrayList<>(); //mas de un rol para cada usuario

//    {
//        "username":"pepito",
//        "password":"1234",
//        "roles":["ADMIN","ROLE_USER","ALGO"]
//    }

}
