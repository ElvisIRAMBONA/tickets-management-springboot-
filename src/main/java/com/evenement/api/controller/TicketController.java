package com.evenement.api.controller;

import com.evenement.api.model.Ticket;
import com.evenement.api.service.TicketService;
import com.evenement.api.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping
    public ResponseEntity<Ticket> acheterTicket(@RequestBody TicketRequest request) {
        Ticket ticket = ticketService.acheterTicket(request.getEvenementId(), request.getUtilisateurId());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Ticket>> getTicketsByEvenement(@PathVariable Long evenementId) {
        return ResponseEntity.ok(ticketRepository.findByEvenementId(evenementId));
    }

    @PostMapping("/verify/{numeroTicket}")
    public ResponseEntity<Ticket> validerTicket(@PathVariable String numeroTicket) {
        Ticket ticket = ticketService.validerTicket(numeroTicket);
        return ResponseEntity.ok(ticket);
    }
}

class TicketRequest {
    private Long evenementId;
    private Long utilisateurId;

    public Long getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}