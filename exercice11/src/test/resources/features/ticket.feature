Feature: Gestion des tickets de support

  Afin de gérer les demandes utilisateurs
  En tant qu'API de support
  Je veux pouvoir créer, consulter et modifier des tickets

  Scenario: Création d'un ticket valide

    Given un utilisateur crée un ticket avec le titre "Connexion VPN impossible" et la priorité HIGH
    When le ticket est créé
    Then le ticket est enregistré avec le statut OPEN
    And le ticket contient le titre "Connexion VPN impossible"


  Scenario: Résolution d'un ticket

    Given un ticket existant avec le statut OPEN
    When le statut du ticket est modifié en RESOLVED
    Then le ticket a le statut RESOLVED


  Scenario: Refus de modification d'un ticket déjà résolu

    Given un ticket existant avec le statut RESOLVED
    When on tente de modifier son statut en IN_PROGRESS
    Then une erreur de type CONFLICT est retournée


  Scenario: Consultation d'un ticket inexistant

    Given un ticket avec l'id 999
    When on consulte ce ticket
    Then une erreur NOT_FOUND est retournée