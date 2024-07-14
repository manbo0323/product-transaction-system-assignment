package com.manbo.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.common.util.exception.ApplicationException;
import com.manbo.user.controller.base.BaseControllerTest;
import com.manbo.user.dto.PurchaseReqDTO;
import com.manbo.user.dto.UserListDTO;
import com.manbo.user.dto.UserPurchaseDTO;
import com.manbo.user.dto.UserRechargeDTO;
import com.manbo.user.dto.UserRechargeReqDTO;
import com.manbo.user.exception.UserErrorEnum;
import com.manbo.user.service.UserService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author manboyu
 */
@WebMvcTest(controllers = UserController.class)
@DisplayName("UserController")
public class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private Long userId;
    private UserRechargeReqDTO userRechargeReqDTO;
    private PurchaseReqDTO purchaseReqDTO;

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.userId = 1L;
        this.userRechargeReqDTO = UserRechargeReqDTO.builder()
            .currency("USD")
            .amount(BigDecimal.TEN)
            .build();
        this.purchaseReqDTO = PurchaseReqDTO.builder()
            .merchantId(1L)
            .sku("SKU_SIZE_L")
            .quantity(10)
            .currency("USD")
            .build();
    }

    @Test
    public void testListUser_shouldReturnSuccess() throws Exception {
        final List<UserListDTO> users = objectMapper.readValue(inputStream("dummy/list_user.json"), new TypeReference<>() {
        });
        final QueryDTO queryDTO = new QueryDTO();
        final PageDTO<UserListDTO> result = PageDTO.<UserListDTO>builder()
            .list(users)
            .pageNumber(queryDTO.getPageNumber())
            .pageSize(queryDTO.getPageSize())
            .totalCount(2L)
            .build();

        given(userService.listUser(queryDTO)).willReturn(result);
        final MvcResult mvcResult = mockMvc.perform(post("/users/list")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andReturn();
        then(userService).should(times(1)).listUser(queryDTO);

        final JsonNode data = retrieveResponseData(mvcResult.getResponse().getContentAsString());
        final PageDTO<UserListDTO> pageData = objectMapper.readValue(data.traverse(), new TypeReference<>() {
        });

        assertThat(pageData).isInstanceOf(PageDTO.class);
        assertThat(pageData.getTotalCount()).isEqualTo(2L);
        assertThat(pageData.getList()).isInstanceOf(List.class).hasSize(2);
    }

    @ParameterizedTest
    @EnumSource(value = UserErrorEnum.class, names = {"USER_NOT_FOUND", "USER_INACTIVE", "USER_CURRENCY_NOT_FOUND"})
    public void testRecharge_givenException_shouldThrow(UserErrorEnum error) throws Exception {
        given(userService.recharge(userId, userRechargeReqDTO)).willThrow(error.exception());
        mockMvc.perform(
                put("/users/{id}/recharge", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRechargeReqDTO))
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.success").value(Boolean.FALSE))
            .andExpect(result -> assertInstanceOf(ApplicationException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), error.getErrorCode()));
        then(userService).should(times(1)).recharge(userId, userRechargeReqDTO);
    }

    @Test
    public void testRecharge_shouldSuccess() throws Exception {
        final UserRechargeDTO result = generator.nextObject(UserRechargeDTO.class);
        result.setBalance(BigDecimal.TEN);

        given(userService.recharge(userId, userRechargeReqDTO)).willReturn(result);
        mockMvc.perform(
                put("/users/{id}/recharge", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRechargeReqDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andExpect(jsonPath("$.data.userId").value(result.getUserId()))
            .andExpect(jsonPath("$.data.name").value(result.getName()))
            .andExpect(jsonPath("$.data.email").value(result.getEmail()))
            .andExpect(jsonPath("$.data.currency").value(result.getCurrency()))
            .andExpect(jsonPath("$.data.balance").value(result.getBalance()));
        then(userService).should(times(1)).recharge(userId, userRechargeReqDTO);
    }

    @ParameterizedTest
    @EnumSource(value = UserErrorEnum.class, names = {"USER_NOT_FOUND", "USER_INACTIVE", "INSUFFICIENT_PRODUCT_INVENTORY", "INSUFFICIENT_BALANCE"})
    public void testPurchase_givenException_shouldThrow(UserErrorEnum error) throws Exception {
        given(userService.purchase(userId, purchaseReqDTO)).willThrow(error.exception());
        mockMvc.perform(
                post("/users/{id}/purchase", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(purchaseReqDTO))
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.success").value(Boolean.FALSE))
            .andExpect(result -> assertInstanceOf(ApplicationException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), error.getErrorCode()));
        then(userService).should(times(1)).purchase(userId, purchaseReqDTO);
    }

    @Test
    public void testPurchase_shouldSuccess() throws Exception {
        final UserPurchaseDTO result = generator.nextObject(UserPurchaseDTO.class);
        result.setBalance(BigDecimal.TEN);
        result.setPurchasePrice(BigDecimal.TEN);

        given(userService.purchase(userId, purchaseReqDTO)).willReturn(result);
        mockMvc.perform(
                post("/users/{id}/purchase", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(purchaseReqDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(Boolean.TRUE))
            .andExpect(jsonPath("$.data.userId").value(result.getUserId()))
            .andExpect(jsonPath("$.data.name").value(result.getName()))
            .andExpect(jsonPath("$.data.email").value(result.getEmail()))
            .andExpect(jsonPath("$.data.currency").value(result.getCurrency()))
            .andExpect(jsonPath("$.data.balance").value(result.getBalance()))
            .andExpect(jsonPath("$.data.purchasePrice").value(result.getPurchasePrice()))
            .andExpect(jsonPath("$.data.productName").value(result.getProductName()));
        then(userService).should(times(1)).purchase(userId, purchaseReqDTO);
    }
}
