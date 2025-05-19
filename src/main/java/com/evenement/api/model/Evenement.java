package com.evenement.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "evenements", indexes = {@Index(name = "idx_date", columnList = "date")})
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotNull(message = "La date est requise")
    @FutureOrPresent(message = "La date doit être dans le présent ou le futur")
    private LocalDateTime date;

    @NotBlank(message = "Le lieu est requis")
    private String lieu;

    @Min(value = 1, message = "La capacité doit être positive")
    private int capacite;

    @Min(value = 0, message = "Le prix doit être non négatif")
    private double prixTicket;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private APP_USER user;
}




