package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderSteps {
    private ProductRepository productRepository;
    private OrderService orderService;
    private OrderReceipt receipt;
    private Exception exception;
    private String currentEmail;
    private CustomerProfile currentProfile;

    private void ensureRepository() {
        if (productRepository == null) {
            productRepository = mock(ProductRepository.class);
            orderService = new OrderService(productRepository);
        }
    }

    @Given("un produit existe avec la référence {string}, nom {string}, prix {double} et stock {int}")
    public void unProduitExisteAvecLaReference(String ref, String name, double price, int stock) {
        ensureRepository();

        Product p = new Product(ref, name, price, stock);
        when(productRepository.findByReference(ref)).thenReturn(Optional.of(p));
    }

    @Given("aucun produit avec la référence {string} n'existe")
    public void aucunProduitAvecLaReference(String ref) {
        ensureRepository();

        when(productRepository.findByReference(ref)).thenReturn(Optional.empty());
    }

    @Given("un client ayant le profil {string} et l'email {string}")
    public void unClientAyantLeProfil(String profile, String email) {
        this.currentEmail = email;
        this.currentProfile = CustomerProfile.valueOf(profile);
    }

    @When("le client commande {int} de la référence {string}")
    public void leClientCommande(int quantity, String ref) {
        try {
            OrderRequest req = new OrderRequest(currentEmail, ref, quantity, currentProfile);
            OrderResponse response = orderService.processOrder(req, currentProfile);
            if (response.isAccepted()) {
                receipt = response.getReceipt();
                exception = null;
            } else {
                receipt = null;
                exception = new Exception(response.getMessage());
            }
        } catch (Exception e) {
            exception = e;
            receipt = null;
        }
    }

    @Then("la commande est acceptée")
    public void laCommandeEstAcceptee() {
        assertNull(exception);
        assertNotNull(receipt);
        assertEquals("Commande acceptée", receipt.getMessage());
    }

    @Then("le montant total doit être {double}")
    public void leMontantTotalDoitEtre(double expectedTotal) {
        assertNotNull(receipt);
        assertEquals(expectedTotal, receipt.getTotal(), 0.001);
    }

    @Then("la commande est refusée avec le message {string}")
    public void laCommandeEstRefuseeAvecLeMessage(String expectedMessage) {
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }
}

