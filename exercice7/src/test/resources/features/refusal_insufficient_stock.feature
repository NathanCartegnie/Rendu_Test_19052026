Feature: Commande refusée - stock insuffisant

  Scenario: Commande refusée si le stock est insuffisant
    Given un produit existe avec la référence "P2", nom "Produit 2", prix 50.0 et stock 1
    When le client commande 2 de la référence "P2"
    Then la commande est refusée avec le message "Stock insuffisant"

