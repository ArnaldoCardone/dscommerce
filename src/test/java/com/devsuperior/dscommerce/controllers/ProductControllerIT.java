package com.devsuperior.dscommerce.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional  //Após executar os testes, o rollback é feito automaticamente
public class ProductControllerIT {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;
    
    private String productName;
    private String adminToken, clientToken, invalidToken;
    private ProductDTO productDTO;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        productName = "Macbook";
        adminToken = tokenUtil.obtainAccessToken(mockMvc,"alex@gmail.com","123456");
        clientToken = tokenUtil.obtainAccessToken(mockMvc,"maria@gmail.com","123456");
        invalidToken = adminToken+ "adaksdjaklsdj"; //Simulando um token inválido

        //Cria um novo produto
        Category category = new Category(2L,"Eletro");
        product = new Product(null,"PlayStation 5","Descrição do produto",1250.0,"http://imagem.produto.com");
        product.getCategories().add(category);
        //Gera o DTO do produto
        productDTO = new ProductDTO(product);
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
        
        ResultActions result = mockMvc
                   .perform(get("/products?name={productName}", productName)
                   .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[0].imgUrl").exists());
    }

    @Test
    public void findAllShouldReturnPageWhenNameIsEmpty() throws Exception {
        
        ResultActions result = mockMvc
                   .perform(get("/products")
                   .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void insertShouldReturnProductDTOWhenAdminLoggedDataIsValid() throws Exception {
        
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
                   .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(26L));
        result.andExpect(jsonPath("$.name").value("PlayStation 5"));
        result.andExpect(jsonPath("$.price").value(1250.0));
        result.andExpect(jsonPath("$.imgUrl").exists());
        //Como categoria é uma lista acesso o primeiro elemento para validar
        result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedInvalidName() throws Exception {
        
        //Gera a condição de erro para o nome
        product.setName("ab");
        productDTO = new ProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnprocessableEntity());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedInvalidDescription() throws Exception {
        
        //Gera a condição de erro para o nome
        product.setDescription("ab");
        productDTO = new ProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnprocessableEntity());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedPriceNegative() throws Exception {
        
        //Gera a condição de erro para o nome
        product.setPrice(-12.0);
        productDTO = new ProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnprocessableEntity());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedPriceZero() throws Exception {
        
        //Gera a condição de erro para o nome
        product.setPrice(0.0);
        productDTO = new ProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnprocessableEntity());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedNoCategories() throws Exception {
        
        //Gera a condição de erro para o nome
        product.getCategories().clear();
        productDTO = new ProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + adminToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnprocessableEntity());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnForbidenWhenClientLogged() throws Exception {
        
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + clientToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isForbidden());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void insertShouldReturnUnathorizesWhenInvalidToken() throws Exception {
        
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                   .perform(post("/products")
                   .header("Authorization", "Bearer " + invalidToken)
                   .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON));
                   

        result.andExpect(status().isUnauthorized());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void deleteShouldReturnOkWhenAdminLoggedValidProduct() throws Exception {
        
               
        ResultActions result = mockMvc
                   .perform(delete("/products/1")
                   .header("Authorization", "Bearer " + adminToken)
                   .accept(MediaType.APPLICATION_JSON));
                   
        result.andExpect(status().isNoContent());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void deleteShouldReturnNotFoundWhenAdminLoggedProductDoesNotExist() throws Exception {
        
               
        ResultActions result = mockMvc
                   .perform(delete("/products/1000")
                   .header("Authorization", "Bearer " + adminToken)
                   .accept(MediaType.APPLICATION_JSON));
                   
        result.andExpect(status().isNotFound());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnDatabaseExceptionWhenAdminLoggedDependentProduct() throws Exception {
                       
        ResultActions result = mockMvc
                   .perform(delete("/products/3")
                   .header("Authorization", "Bearer " + adminToken)
                   .accept(MediaType.APPLICATION_JSON));
                   
        result.andExpect(status().isBadRequest());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void deleteShouldReturnForbidenWhenUserLogged() throws Exception {
        
               
        ResultActions result = mockMvc
                   .perform(delete("/products/1")
                   .header("Authorization", "Bearer " + clientToken)
                   .accept(MediaType.APPLICATION_JSON));
                   
        result.andExpect(status().isForbidden());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }

    @Test
    public void deleteShouldReturnUnathorizedWhenInvalidToken() throws Exception {
        
               
        ResultActions result = mockMvc
                   .perform(delete("/products/1")
                   .header("Authorization", "Bearer " + invalidToken)
                   .accept(MediaType.APPLICATION_JSON));
                   
        result.andExpect(status().isUnauthorized());
        //result.andExpect(jsonPath("$.categories[0].id").value(2L)); 
    }
}
