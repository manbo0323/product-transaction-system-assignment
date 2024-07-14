package com.manbo.merchant.model.converter;

import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductReqDTO;
import com.manbo.merchant.dto.MerchantReconcileDTO;
import com.manbo.merchant.dto.MerchantReconcileDTO.PurchaseLogDTO;
import com.manbo.merchant.dto.MerchantReconcileDTO.SummaryPurchaseDTO;
import com.manbo.merchant.model.MerchantDO;
import com.manbo.merchant.model.MerchantProductDO;
import com.manbo.merchant.model.MerchantPurchaseLogDO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Manbo
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true))
public interface MerchantConverter {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "quantity", ignore = true)
    void populate(Long merchantId, MerchantProductReqDTO reqDTO, @MappingTarget MerchantProductDO data);

    MerchantProductDTO toDTO(MerchantProductDO data);

    @Mapping(target = "merchantId", source = "id")
    @Mapping(target = "merchantName", source = "name")
    @Mapping(target = "merchantEmail", source = "email")
    MerchantReconcileDTO toReconcileDTO(MerchantDO merchant);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    SummaryPurchaseDTO toSummaryPurchaseDTO(MerchantProductDO product);

    PurchaseLogDTO toPurchaseLogDTO(MerchantPurchaseLogDO data);
}
