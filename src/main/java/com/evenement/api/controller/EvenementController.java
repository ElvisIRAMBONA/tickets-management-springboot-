
package com.evenement.api.controller;

import com.evenement.api.model.Evenement;
import com.evenement.api.model.APP_USER;
import com.evenement.api.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private EvenementRepository repository;

    @GetMapping
    public List<Evenement> getAllEvenements() {
        return repository.findAll();
    }

    @PostMapping
    public Evenement createEvenement(@RequestBody Evenement evenement) {
        APP_USER user = (APP_USER) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        evenement.setUser(user);
        return repository.save(evenement);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        return repository.findById(id)
                .map(evenement -> ResponseEntity.ok(evenement))
                .orElse(ResponseEntity.notFound().build());
    }
}
