package es.daw.productoapirest.controller;

import es.daw.productoapirest.dto.ApiResponse;
import es.daw.productoapirest.dto.AuthRequest;
import es.daw.productoapirest.dto.AuthResponse;
import es.daw.productoapirest.dto.RegisterRequest;
import es.daw.productoapirest.entity.Role;
import es.daw.productoapirest.entity.User;
import es.daw.productoapirest.repository.RoleRepository;
import es.daw.productoapirest.repository.UserRepository;
import es.daw.productoapirest.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        //1º comprobar si el usuario ya existe (nosotros usar el service)
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) //409 porque ya existe el usuario
                    .body(new ApiResponse(false, "el username " + request.getUsername() + " ya existe"));

        }
        //2º determinar el rol solicitado o por defecto

        String roleName = Optional.ofNullable(request.getRole())
                .map(String::toUpperCase)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .orElse("ROLE_USER"); //por defecto
        //PENDIENTE: y si se envian roles duplicados
        //3º Buscar rol en la BD
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("El rol " + roleName + " no existe"));

        //4º Crear nuevo usuario
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.addRole(role);
        //newUser.getRoles().add(role);

        userRepository.save(newUser);

        //5º respuesta detallada
        ApiResponse response = new ApiResponse(true, "Usuario registrado correctamente");
        response.addDetail("username", newUser.getUsername());
       //response.addDetail("roles", newUser.getRoles().toString());
        response.addDetail("roles",newUser.getRoles().stream()
                .map(Role::getName)
                .toList()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        /*
            authenticationManager.authenticate(...) es el punto central de validación en Spring Security.
            Internamente llama al UserDetailsService.loadUserByUsername().
            Compara la contraseña ingresada con la almacenada (mediante el PasswordEncoder).
            Si las credenciales son correctas, devuelve un Authentication lleno de datos del usuario.
            Si no lo son, lanza una excepción (BadCredentialsException).
         */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // PENDIENTE!!!! CONTROLAR LA EXCEPCIÓN BADCREDENTIALS...
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // PENDIENTE MEJORAS EN EL REGISTRO
    // 1. CREAR UN AUTHSERVICE Y QUITAR LA LÓGICA DE ESTE ENDPOINT (REPOSITORY ECT...)
    // 2. TRABAJAR CON UserAlreadyExistsException Y RoleNotFoundException

}
