package com.manbo.merchant.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.common.util.exception.ApplicationException;
import com.manbo.merchant.controller.base.BaseControllerTest;
import com.manbo.merchant.dto.MerchantListDTO;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductDTO.SimpleMerchantDTO;
import com.manbo.merchant.dto.MerchantProductReqDTO;
import com.manbo.merchant.exception.MerchantErrorEnum;
import com.manbo.merchant.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author manboyu
 */
@WebMvcTest(controllers = MerchantController.class)
@DisplayName("MerchantController")
public class MerchantControllerTest extends BaseControllerTest {

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private MockMvc mockMvc;

    private Long merchantId;
    private Long productId;
    private String sku;

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.merchantId = 1L;
        this.productId = 1L;
        this.sku = "SKU_SIZE_L";
    }

    @Test
    public void testListMerchant_shouldReturnSuccess() throws Exception {
        final List<MerchantListDTO> merchants = objectMapper.readValue(inputStream("dummy/list_merchant.json"), new TypeReference<>() {
        });
        final QueryDTO queryDTO = new QueryDTO();
        final PageDTO<MerchantListDTO> result = PageDTO.<MerchantListDTO>builder()
            .list(merchants)
            .pageNumber(queryDTO.getPageNumber())
            .pageSize(queryDTO.getPageSize())
            .totalCount(2L)
            .build();

        given(merchantService.listMerchant(queryDTO)).willReturn(result);
        final MvcResult mvcResult = mockMvc.perform(post("/merchants/list")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andReturn();
        then(merchantService).should(times(1)).listMerchant(queryDTO);

        final JsonNode data = retrieveResponseData(mvcResult.getResponse().getContentAsString());
        final PageDTO<MerchantListDTO> pageData = objectMapper.readValue(data.traverse(), new TypeReference<>() {
        });

        assertThat(pageData).isInstanceOf(PageDTO.class);
        assertThat(pageData.getTotalCount()).isEqualTo(2L);
        assertThat(pageData.getList()).isInstanceOf(List.class).hasSize(2);
    }

    @Test
    public void testListProduct_shouldReturnSuccess() throws Exception {
        final List<MerchantProductDTO> products = objectMapper.readValue(inputStream("dummy/list_product.json"), new TypeReference<>() {
        });
        final QueryDTO queryDTO = new QueryDTO();
        final PageDTO<MerchantProductDTO> result = PageDTO.<MerchantProductDTO>builder()
            .list(products)
            .pageNumber(queryDTO.getPageNumber())
            .pageSize(queryDTO.getPageSize())
            .totalCount(5L)
            .build();

        given(merchantService.listProduct(queryDTO)).willReturn(result);
        final MvcResult mvcResult = mockMvc.perform(post("/merchants/products/list")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andReturn();
        then(merchantService).should(times(1)).listProduct(queryDTO);

        final JsonNode data = retrieveResponseData(mvcResult.getResponse().getContentAsString());
        final PageDTO<MerchantListDTO> pageData = objectMapper.readValue(data.traverse(), new TypeReference<>() {
        });

        assertThat(pageData).isInstanceOf(PageDTO.class);
        assertThat(pageData.getTotalCount()).isEqualTo(5L);
        assertThat(pageData.getList()).isInstanceOf(List.class).hasSize(5);
    }

    @Test
    public void testAddProductToMerchant_shouldReturnSuccess() throws Exception {
        final MerchantProductReqDTO reqDTO = generator.nextObject(MerchantProductReqDTO.class);
        final MerchantProductDTO result = generator.nextObject(MerchantProductDTO.class);
        result.setPrice(BigDecimal.TEN);

        given(merchantService.addProductToMerchant(merchantId, reqDTO)).willReturn(result);
        final SimpleMerchantDTO merchant = result.getMerchant();
        mockMvc.perform(
                post("/merchants/{merchantId}/products", merchantId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andExpect(jsonPath("$.data.sku").value(result.getSku()))
            .andExpect(jsonPath("$.data.name").value(result.getName()))
            .andExpect(jsonPath("$.data.price").value(result.getPrice()))
            .andExpect(jsonPath("$.data.currency").value(result.getCurrency()))
            .andExpect(jsonPath("$.data.quantity").value(result.getQuantity()))
            .andExpect(jsonPath("$.data.status").value(result.getStatus().name()))
            .andExpect(jsonPath("$.data.merchant.id").value(merchant.getId()))
            .andExpect(jsonPath("$.data.merchant.name").value(merchant.getName()))
            .andExpect(jsonPath("$.data.merchant.email").value(merchant.getEmail()));
        then(merchantService).should(times(1)).addProductToMerchant(merchantId, reqDTO);
    }

    @Test
    public void testFindProductBy_givenException_shouldThrow() throws Exception {
        given(merchantService.findProductBy(merchantId, sku)).willThrow(MerchantErrorEnum.PRODUCT_NOT_FOUND.exception());
        mockMvc.perform(
                get("/merchants/{merchantId}/products", merchantId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("sku", sku)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(Boolean.FALSE))
            .andExpect(result -> assertInstanceOf(ApplicationException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), MerchantErrorEnum.PRODUCT_NOT_FOUND.getErrorCode()));
        then(merchantService).should(times(1)).findProductBy(merchantId, sku);
    }

    @Test
    public void testFindProductBy_shouldReturnSuccess() throws Exception {
        final MerchantProductDTO result = generator.nextObject(MerchantProductDTO.class);
        result.setPrice(BigDecimal.TEN);

        given(merchantService.findProductBy(merchantId, sku)).willReturn(result);
        mockMvc.perform(
                get("/merchants/{merchantId}/products", merchantId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("sku", sku)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andExpect(jsonPath("$.data.sku").value(result.getSku()))
            .andExpect(jsonPath("$.data.name").value(result.getName()))
            .andExpect(jsonPath("$.data.price").value(result.getPrice()))
            .andExpect(jsonPath("$.data.currency").value(result.getCurrency()))
            .andExpect(jsonPath("$.data.quantity").value(result.getQuantity()))
            .andExpect(jsonPath("$.data.status").value(result.getStatus().name()));
        then(merchantService).should(times(1)).findProductBy(merchantId, sku);
    }

    @ParameterizedTest
    @EnumSource(value = MerchantErrorEnum.class, names = {"MERCHANT_ACCOUNT_NOT_FOUND", "MERCHANT_PRODUCT_QUANTITY_FAILED"})
    public void testPlaceOrder_givenException_shouldThrow(MerchantErrorEnum error) throws Exception {
        willThrow(error.exception()).willDoNothing().given(merchantService).placeOrder(merchantId, productId, BigDecimal.TEN, "USD", 1);
        mockMvc.perform(
                post("/merchants/{merchantId}/products/{productId}/place-order", merchantId, productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("purchasePrice", "10")
                    .queryParam("quantity", "1")
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.success").value(Boolean.FALSE))
            .andExpect(result -> assertInstanceOf(ApplicationException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), error.getErrorCode()));
        then(merchantService).should(times(1)).placeOrder(merchantId, productId, BigDecimal.TEN, "USD", 1);
    }

    @Test
    public void testPlaceOrder_shouldReturnSuccess() throws Exception {
        willDoNothing().given(merchantService).placeOrder(merchantId, productId, BigDecimal.TEN, "USD", 1);
        mockMvc.perform(
                post("/merchants/{merchantId}/products/{productId}/place-order", merchantId, productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("purchasePrice", "10")
                    .queryParam("quantity", "1")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andExpect(jsonPath("$.data", is(nullValue())));
        then(merchantService).should(times(1)).placeOrder(merchantId, productId, BigDecimal.TEN, "USD", 1);
    }
}
