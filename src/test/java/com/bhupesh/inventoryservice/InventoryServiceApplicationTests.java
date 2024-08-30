package com.bhupesh.inventoryservice;

import com.bhupesh.inventoryservice.model.Inventory;
import com.bhupesh.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceApplicationTests.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:9.0.1");

    @DynamicPropertySource
    static void setMySQLProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void shouldInformIsInStock() throws Exception {
        String skuCode = "iphone_15";
        Inventory inventory = Inventory.builder()
                .skuCode(skuCode)
                .quantity(100)
                .build();
        inventoryRepository.save(inventory);

        String response1 = mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                        .param("skuCode", skuCode)
                        .param("quantity", "100"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("true", response1);

        String response2 = mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                        .param("skuCode", skuCode)
                        .param("quantity", "1000"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("false", response2);

        log.info(inventoryRepository.findQuantityBySkuCode(skuCode).toString());
    }

}
