package com.evenement.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private APP_USER utilisateur;

    private LocalDateTime dateAchat;
    private String statut;
    private String numeroTicket;
    private String qrCode;

    public Ticket() {
        this.numeroTicket = UUID.randomUUID().toString();
        this.dateAchat = LocalDateTime.now();
        this.statut = "VALIDÉ";
    }
}