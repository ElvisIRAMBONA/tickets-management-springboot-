package com.evenement.api.controller;

import com.evenement.api.model.APP_USER;
import com.evenement.api.repository.UserRepository;
import com.evenement.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Enregistre un nouvel utilisateur
     * @param request Requête contenant le nom d'utilisateur et le mot de passe
     * @return L'utilisateur enregistré ou un message d'erreur
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("Tentative d'enregistrement pour username: " + request.getUsername());
        Optional<APP_USER> existingUser = userRepository.findByUsername(request.getUsername());
        System.out.println("Utilisateur existant: " + (existingUser.isPresent() ? existingUser.get().getUsername() : "null"));
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nom d'utilisateur déjà pris");
        }

        APP_USER user = new APP_USER();
        user.setUsername(request.getUsername());
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        System.out.println("Mot de passe haché: " + hashedPassword);
        user.setPassword(hashedPassword);
        APP_USER savedUser = userRepository.save(user);
        System.out.println("Utilisateur enregistré: " + savedUser.getUsername());

        return ResponseEntity.ok(savedUser);
    }

    /**
     * Connecte un utilisateur et retourne un token JWT
     * @param request Requête contenant le nom d'utilisateur et le mot de passe
     * @return Le token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Tentative de connexion pour username: " + request.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            System.out.println("Token généré pour username: " + request.getUsername());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            System.out.println("Échec de l'authentification pour username: " + request.getUsername() + ", erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Échec de l'authentification: " + e.getMessage());
        }
    }
}

class RegisterRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}