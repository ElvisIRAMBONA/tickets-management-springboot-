package com.evenement.api.repository;

import com.evenement.api.model.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEvenementId(Long evenementId);
    List<Ticket> findByUtilisateurId(Long utilisateurId);
    Optional<Ticket> findByNumeroTicket(String numeroTicket);
}