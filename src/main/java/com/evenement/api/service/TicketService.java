package com.evenement.api.service;

import com.evenement.api.model.Evenement;
import com.evenement.api.model.Ticket;
import com.evenement.api.model.APP_USER;
import com.evenement.api.repository.EvenementRepository;
import com.evenement.api.repository.TicketRepository;
import com.evenement.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EvenementRepository evenementRepository;
    @Autowired
    private UserRepository utilisateurRepository;

    public Ticket acheterTicket(Long evenementId, Long utilisateurId) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        APP_USER utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        long ticketsVendus = ticketRepository.findByEvenementId(evenementId).size();
        if (ticketsVendus >= evenement.getCapacite()) {
            throw new RuntimeException("Événement complet");
        }

        Ticket ticket = new Ticket();
        ticket.setEvenement(evenement);
        ticket.setUtilisateur(utilisateur);
        ticket.setQrCode("https://monapp.com/verify?ticket=" + ticket.getNumeroTicket());

        return ticketRepository.save(ticket);
    }

    public Ticket validerTicket(String numeroTicket) {
        Ticket ticket = ticketRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        if (!ticket.getStatut().equals("VALIDÉ")) {
            throw new RuntimeException("Ticket non valide");
        }
        ticket.setStatut("UTILISÉ");
        return ticketRepository.save(ticket);
    }
}