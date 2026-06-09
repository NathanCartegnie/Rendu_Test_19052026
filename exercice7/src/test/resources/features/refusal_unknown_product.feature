Feature: Commande refusée - produit inconnu

  Scenario: Commande refusée si le produit est inconnu
    Given aucun produit avec la référence "UNKNOWN" n'existe
    When le client commande 1 de la référence "UNKNOWN"
    Then la commande est refusée avec le message "Produit inconnu"

