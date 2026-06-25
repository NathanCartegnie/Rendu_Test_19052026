package org.example.ticketsupport.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.ticketsupport.dto.TicketCreateRequest;
import org.example.ticketsupport.dto.UpdateStatusRequest;
import org.example.ticketsupport.model.Priority;
import org.example.ticketsupport.model.TicketStatus;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TicketSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String response;
    private Long ticketId;

    private int lastStatusCode;
    private Long currentTicketId;

    @Given("un utilisateur crée un ticket avec le titre {string} et la priorité HIGH")
    public void create_ticket(String title) throws Exception {

        TicketCreateRequest request =
                new TicketCreateRequest(title, Priority.HIGH);

        response = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ticketId = objectMapper.readTree(response)
                .get("id")
                .asLong();
    }

    @When("le ticket est créé")
    public void ticket_created() {
        // rien à faire
    }

    @Then("le ticket est enregistré avec le statut OPEN")
    public void check_status_open() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Then("le ticket contient le titre {string}")
    public void check_title(String title) throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title));
    }

    @Given("un ticket existant avec le statut OPEN")
    public void existing_ticket_open() throws Exception {
        create_ticket("Ticket test");
    }

    @When("le statut du ticket est modifié en RESOLVED")
    public void update_to_resolved() throws Exception {

        UpdateStatusRequest request =
                new UpdateStatusRequest(TicketStatus.RESOLVED);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Then("le ticket a le statut RESOLVED")
    public void check_resolved() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Given("un ticket existant avec le statut RESOLVED")
    public void existing_ticket_resolved() throws Exception {

        create_ticket("Ticket résolu");

        UpdateStatusRequest request =
                new UpdateStatusRequest(TicketStatus.RESOLVED);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        currentTicketId = ticketId;
    }

    @When("on tente de modifier son statut en IN_PROGRESS")
    public void try_update_resolved_ticket() throws Exception {

        UpdateStatusRequest request =
                new UpdateStatusRequest(TicketStatus.IN_PROGRESS);

        lastStatusCode = mockMvc.perform(
                        patch("/api/tickets/" + currentTicketId + "/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getStatus();
    }

    @Then("une erreur de type CONFLICT est retournée")
    public void conflict_returned() {
        Assertions.assertEquals(409, lastStatusCode);
    }

    @Given("un ticket avec l'id {int}")
    public void non_existing_ticket(Integer id) {
        currentTicketId = id.longValue();
    }

    @When("on consulte ce ticket")
    public void consult_ticket() throws Exception {

        lastStatusCode = mockMvc.perform(
                        get("/api/tickets/" + currentTicketId))
                .andReturn()
                .getResponse()
                .getStatus();
    }

    @Then("une erreur NOT_FOUND est retournée")
    public void not_found_returned() {
        Assertions.assertEquals(404, lastStatusCode);
    }
}