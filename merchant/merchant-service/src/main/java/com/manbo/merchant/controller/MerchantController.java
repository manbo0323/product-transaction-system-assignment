package com.manbo.merchant.controller;

import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.merchant.dto.MerchantListDTO;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductReqDTO;
import com.manbo.merchant.resource.MerchantResource;
import com.manbo.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MerchantController implements MerchantResource {

    private final MerchantService merchantService;

    @Override
    @Operation(summary = "Merchant list")
    public PageDTO<MerchantListDTO> listMerchant(final QueryDTO queryDTO) {
        return merchantService.listMerchant(queryDTO);
    }

    @Override
    @Operation(summary = "Product list")
    public PageDTO<MerchantProductDTO> listProduct(final QueryDTO queryDTO) {
        return merchantService.listProduct(queryDTO);
    }

    @Override
    @Operation(summary = "Add Product to Merchant")
    public MerchantProductDTO addProductToMerchant(final Long merchantId, @Valid final MerchantProductReqDTO reqDTO) {
        return merchantService.addProductToMerchant(merchantId, reqDTO);
    }

    @Override
    @Operation(summary = "Find Product By MerchantId and SKU")
    public MerchantProductDTO findProductBy(final Long merchantId, final String sku) {
        return merchantService.findProductBy(merchantId, sku);
    }

    @Override
    @Operation(summary = "Place Order")
    public void placeOrder(final Long merchantId, final Long productId, final BigDecimal purchasePrice, final String currency, final Integer quantity) {
        merchantService.placeOrder(merchantId, productId, purchasePrice, currency, quantity);
    }
}
