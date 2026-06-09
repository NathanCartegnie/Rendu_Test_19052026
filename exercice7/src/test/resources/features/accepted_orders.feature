Feature: Commandes acceptées

  Scenario Outline: Commande acceptée selon profil client
    Given un produit existe avec la référence "P1", nom "Produit 1", prix 100.0 et stock 10
    And un client ayant le profil "<profile>" et l'email "client@example.com"
    When le client commande 2 de la référence "P1"
    Then la commande est acceptée
    And le montant total doit être <total>

    Examples:
      | profile  | total |
      | STANDARD | 200.0 |
      | PREMIUM  | 180.0 |
      | VIP      | 160.0 |

