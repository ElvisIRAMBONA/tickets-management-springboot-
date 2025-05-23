package com.evenement.api.controller;

   import com.evenement.api.model.Evenement;
   import com.evenement.api.model.APP_USER;
   import com.evenement.api.repository.EvenementRepository;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.http.HttpStatus;
   import org.springframework.security.core.context.SecurityContextHolder;
   import org.springframework.security.core.userdetails.UserDetailsService;
   import org.springframework.security.core.userdetails.UsernameNotFoundException;
   import org.springframework.web.bind.annotation.*;
   import org.springframework.web.server.ResponseStatusException;

   import java.util.List;

   @RestController
   @RequestMapping("/api/evenements")
   public class EvenementController {

       private static final Logger logger = LoggerFactory.getLogger(EvenementController.class);

       @Autowired
       private EvenementRepository evenementRepository;

       @Autowired
       private UserDetailsService userDetailsService;

       @GetMapping
       public List<Evenement> getAllEvenements() {
           logger.info("Requête GET /api/evenements reçue");
           List<Evenement> evenements = evenementRepository.findAll();
           logger.debug("Événements récupérés : {}", evenements);
           return evenements;
       }

       @PostMapping
       public Evenement createEvenement(@RequestBody Evenement evenement) {
           logger.info("Requête POST /api/evenements reçue");
           String username = SecurityContextHolder.getContext().getAuthentication().getName();
           if (username == null || username.isEmpty() || username.equals("anonymousUser")) {
               logger.error("Aucun utilisateur authentifié trouvé");
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
           }
           try {
               APP_USER user = (APP_USER) userDetailsService.loadUserByUsername(username);
               logger.debug("Utilisateur récupéré : {}", username);
               evenement.setUser(user);
               Evenement savedEvenement = evenementRepository.save(evenement);
               logger.debug("Événement créé : {}", savedEvenement);
               return savedEvenement;
           } catch (UsernameNotFoundException e) {
               logger.error("Utilisateur non trouvé : {}", username);
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé : " + username);
           }
       }
   }