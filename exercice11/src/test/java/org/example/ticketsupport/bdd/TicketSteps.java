package org.example.ticketsupport.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ticketsupport.dto.TicketCreateRequest;
import org.example.ticketsupport.dto.UpdateStatusRequest;
import org.example.ticketsupport.model.Priority;
import org.example.ticketsupport.model.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.cucumber.java.en.*;

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
        // rien à faire (déjà créé dans Given)
    }

    @Then("le ticket est enregistré avec le statut OPEN")
    public void check_status_open() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Then("le ticket contient le titre {string}")
    public void check_title(String title) throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId))
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
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}