package com.metao.book.product.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.metao.book.product.domain.ProductEntity;
import com.metao.book.product.domain.dto.ProductDTO;
import com.metao.book.product.domain.mapper.ProductMapper;
import com.metao.book.product.domain.service.ProductService;
import com.metao.book.product.event.ProductCreatedEvent;
import com.metao.book.product.util.ProductTestUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"kafka.isEnabled=false"})
@WebMvcTest(controllers = ProductController.class)
@Import({ProductMapper.class})
class ProductControllerTests {

    private static final String PRODUCT_URL = "/products";

    @MockBean
    KafkaTemplate<String, ProductCreatedEvent> kafkaProductProducer;

    @SpyBean
    ProductMapper productMapper;

    @MockBean
    ProductService productService;

    @Autowired
    MockMvc webTestClient;

    @Test
    void loadOneProductIsNotFound() throws Exception {
        var productId = UUID.randomUUID().toString();
        webTestClient.perform(get(PRODUCT_URL + productId)).andExpect(status().isNotFound());
    }

    @Test
    void getOneProductIsOk() throws Exception {
        var pe = ProductTestUtils.createProductEntity();

        when(productService.getProductByAsin(pe.getAsin())).thenReturn(Optional.of(pe));

        webTestClient.perform(get(String.format("%s/%s", PRODUCT_URL, pe.getAsin()))).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.asin").value(pe.getAsin())).andExpect(jsonPath("$.title").value(pe.getTitle()))
            .andExpect(jsonPath("$.description").value(pe.getDescription()))
            .andExpect(jsonPath("$.categories[0].category").value("category"))
            .andExpect(jsonPath("$.imageUrl").value(pe.getImageUrl())).andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(12)));
    }

    @Test
    void testSaveProductIsOk() throws Exception {
        var productDto = """
            {
                "asin": "1234567890",
                "description": "A sample description",
                "title": "Sample Product",
                "image_url": "https://example.com/image.jpg",
                "price": 99.99,
                "currency": "usd",
                "volume": 1.0,
                "categories": ["book"],
                "also_bought": ["1234567891", "1234567892"]
             }
            """;

        var productRecord = new ProducerRecord<>("product-created", "1234567890",
            ProductCreatedEvent.getDefaultInstance());

        doReturn(CompletableFuture.completedFuture(productRecord)).when(kafkaProductProducer)
            .send(anyString(), anyString(), any(ProductCreatedEvent.class));

        webTestClient.perform(post(PRODUCT_URL).contentType(MediaType.APPLICATION_JSON).content(productDto))
            .andExpect(status().isCreated()).andExpect(content().string("true"));

        verify(productMapper).toEvent(any(ProductDTO.class));
    }

    @Test
    void testGetMultipleProductsIsOk() throws Exception {
        int limit = 10, offset = 0;
        List<ProductEntity> pes = ProductTestUtils.createMultipleProductEntity(limit);

        // Load multiple products and verify responses
        // OPTION 1 - using for-loop and query multiple times
        for (ProductEntity pe : pes) {
            when(productService.getAllProductsPageable(limit, offset)).thenReturn(pes.stream());
            webTestClient.perform(get(String.format("%s?offset=%s&limit=%s", PRODUCT_URL, offset, limit)))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$.[?(@.asin == '" + pe.getAsin() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.title == '" + pe.getTitle() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.description == '" + pe.getDescription() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.imageUrl == '" + pe.getImageUrl() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.currency == '" + pe.getPriceCurrency().toString() + "')]").exists())
                .andExpect(jsonPath("$.[?(@.price == " + pe.getPriceValue() + ")]").exists());
        }

        when(productService.getAllProductsPageable(limit, offset)).thenReturn(pes.stream());

        // OPTION 2 - using for-loop and query once and then verify responses using matcher -- preferable option
        webTestClient.perform(get(String.format("%s?offset=%s&limit=%s", PRODUCT_URL, offset, limit)))
            .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(10))
            .andExpect(jsonPath("$[*].asin", extractFieldFromProducts(pes, ProductEntity::getAsin)))
            .andExpect(jsonPath("$[*].title", extractFieldFromProducts(pes, ProductEntity::getTitle)))
            .andExpect(jsonPath("$[*].description", extractFieldFromProducts(pes, ProductEntity::getDescription)))
            .andExpect(jsonPath("$[*].imageUrl", extractFieldFromProducts(pes, ProductEntity::getImageUrl))).andExpect(
                jsonPath("$[*].currency", extractFieldFromProducts(pes, pe -> pe.getPriceCurrency().toString())));
    }

    @Test
    void testGetMultipleProductsIsOkWithCategories() throws Exception {
        int limit = 10, offset = 0;
        Supplier<List<ProductEntity>> pes = () -> ProductTestUtils.createMultipleProductEntity(limit);

    }

    @Test
    void testGetMultipleProductsIsOkWithAlsoBought() throws Exception {
        int limit = 10, offset = 0;
        Supplier<List<ProductEntity>> pes = () -> ProductTestUtils.createMultipleProductEntity(limit);

    }

    @Test
    void testGetMultipleProductsIsOkWithCategoriesAndAlsoBought() throws Exception {
        int limit = 10, offset = 0;
        Supplier<List<ProductEntity>> pes = () -> ProductTestUtils.createMultipleProductEntity(limit);

    }

    @Test
    void getOneProductIsNotFound() throws Exception {
        var productId = UUID.randomUUID().toString();
        when(productService.getProductByAsin(productId)).thenReturn(Optional.empty());

        webTestClient.perform(get(PRODUCT_URL + productId)).andExpect(status().isNotFound());

        verifyNoMoreInteractions(productService);
    }

    private Matcher<Iterable<? extends String>> extractFieldFromProducts(
        List<ProductEntity> pes, Function<ProductEntity, String> extractor
    ) {
        return Matchers.containsInAnyOrder(pes.stream().map(extractor).toArray(String[]::new));
    }
}
