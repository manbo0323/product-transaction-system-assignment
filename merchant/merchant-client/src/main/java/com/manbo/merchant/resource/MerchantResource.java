package com.manbo.merchant.resource;

import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.merchant.dto.MerchantListDTO;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductReqDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author manboyu
 */
public interface MerchantResource {

    @PostMapping("/merchants/list")
    PageDTO<MerchantListDTO> listMerchant(@RequestBody QueryDTO queryDTO);

    @PostMapping("/merchants/products/list")
    PageDTO<MerchantProductDTO> listProduct(@RequestBody QueryDTO queryDTO);

    @PostMapping("/merchants/{merchantId}/products")
    MerchantProductDTO addProductToMerchant(@PathVariable("merchantId") Long merchantId, @RequestBody MerchantProductReqDTO reqDTO);

    @GetMapping("/merchants/{merchantId}/products")
    MerchantProductDTO findProductBy(@PathVariable("merchantId") Long merchantId, @RequestParam("sku") String sku);

    @PostMapping("/merchants/{merchantId}/products/{productId}/place-order")
    void placeOrder(@PathVariable("merchantId") Long merchantId, @PathVariable("productId") Long productId,
                    @RequestParam("purchasePrice") BigDecimal purchasePrice, @RequestParam(value = "currency", defaultValue = "USD") String currency,
                    @RequestParam("quantity") Integer quantity);
}
