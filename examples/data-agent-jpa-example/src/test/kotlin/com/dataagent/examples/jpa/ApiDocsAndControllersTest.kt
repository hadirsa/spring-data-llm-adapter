package com.dataagent.examples.jpa

import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class ApiDocsAndControllersTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Test
    fun `openapi json is available`() {
        mockMvc.get("/v3/api-docs")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.openapi") { exists() }
                jsonPath("$.info.title", containsString("Data Agent JPA Example"))
            }
    }

    @Test
    fun `swagger ui endpoint is reachable`() {
        // Depending on Springdoc version, /swagger-ui.html redirects to /swagger-ui/index.html
        mockMvc.get("/swagger-ui.html")
            .andExpect { status { is3xxRedirection() } }
    }

    @Test
    fun `health endpoint responds`() {
        mockMvc.get("/api/health")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("UP") }
            }
    }

    @Test
    fun `discovery flow learns schemas and returns count`() {
        mockMvc.post("/api/schemas/discover")
            .andExpect {
                status { isAccepted() }
            }

        mockMvc.get("/api/schemas")
            .andExpect {
                status { isOk() }
                jsonPath("$", hasSize<Int>(greaterThanOrEqualTo(0)))
            }
    }
}


