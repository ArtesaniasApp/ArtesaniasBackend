package cl.duoc.artesanias_backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba de integración: levanta la app con H2 y los datos iniciales, y revisa las reglas de acceso
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    private String login(String email, String password) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return respuesta.split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void catalogo_esPublico() throws Exception {
        mockMvc.perform(get("/api/productos")).andExpect(status().isOk());
    }

    @Test
    void perfil_sinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/usuarios/perfil")).andExpect(status().isUnauthorized());
    }

    @Test
    void perfil_conTokenFalso_responde401() throws Exception {
        mockMvc.perform(get("/api/usuarios/perfil").header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_contrasenaIncorrecta_responde401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@aleyleo.cl\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cliente_noEntraAlPanelAdmin() throws Exception {
        String token = login("cliente@aleyleo.cl", "cliente123");
        mockMvc.perform(get("/api/admin/productos").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_entraAlPanelAdmin() throws Exception {
        String token = login("admin@aleyleo.cl", "admin123");
        mockMvc.perform(get("/api/admin/productos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
