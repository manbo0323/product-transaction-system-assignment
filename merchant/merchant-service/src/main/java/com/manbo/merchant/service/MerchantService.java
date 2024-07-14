package com.manbo.merchant.service;

import com.manbo.common.service.BaseService;
import com.manbo.common.util.dto.BaseDTO;
import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.common.util.enums.CommonStatusEnum;
import com.manbo.common.util.utils.CollectionUtils;
import com.manbo.common.util.utils.DecimalUtils;
import com.manbo.merchant.dao.MerchantAccountDAO;
import com.manbo.merchant.dao.MerchantDAO;
import com.manbo.merchant.dao.MerchantProductDAO;
import com.manbo.merchant.dao.MerchantPurchaseLogDAO;
import com.manbo.merchant.dto.MerchantAccountDTO;
import com.manbo.merchant.dto.MerchantListDTO;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductReqDTO;
import com.manbo.merchant.exception.MerchantErrorEnum;
import com.manbo.merchant.model.MerchantAccountDO;
import com.manbo.merchant.model.MerchantDO;
import com.manbo.merchant.model.MerchantProductDO;
import com.manbo.merchant.model.MerchantPurchaseLogDO;
import com.manbo.merchant.model.converter.MerchantConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author manboyu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService extends BaseService {

    private final MerchantDAO merchantDAO;
    private final MerchantAccountDAO merchantAccountDAO;
    private final MerchantProductDAO merchantProductDAO;
    private final MerchantPurchaseLogDAO merchantPurchaseLogDAO;
    private final MerchantConverter merchantConverter;

    public PageDTO<MerchantListDTO> listMerchant(final QueryDTO queryDTO) {
        final PageDTO<MerchantListDTO> pageData = convert(merchantDAO.findMerchants(queryDTO));
        final List<MerchantListDTO> merchants = pageData.getList();
        final List<Long> merchantIds = CollectionUtils.streamOf(merchants).map(BaseDTO::getId).toList();
        final Map<Long, List<MerchantAccountDTO>> merchantAccountGroup = CollectionUtils.streamOf(merchantAccountDAO.findByMerchantIds(merchantIds))
            .collect(Collectors.groupingBy(MerchantAccountDTO::getMerchantId));
        merchants.forEach(merchant -> merchant.setAccounts(merchantAccountGroup.get(merchant.getId())));
        return pageData;
    }

    public PageDTO<MerchantProductDTO> listProduct(final QueryDTO queryDTO) {
        return convert(merchantProductDAO.findProducts(queryDTO));
    }

    @Transactional(rollbackOn = Exception.class)
    public MerchantProductDTO addProductToMerchant(final Long merchantId, final MerchantProductReqDTO reqDTO) {
        final MerchantDO merchantDO = merchantDAO.findByIdOrThrowException(merchantId);
        final MerchantProductDO merchantProductDO = merchantProductDAO.findByMerchantIdAndSkuAndStatus(merchantDO.getId(), reqDTO.getSku(), CommonStatusEnum.ACTIVE).orElseGet(MerchantProductDO::new);
        merchantConverter.populate(merchantDO.getId(), reqDTO, merchantProductDO);
        final Integer reqQuantity = reqDTO.getQuantity();
        if (merchantProductDO.isNew()) {
            merchantProductDO.setQuantity(reqQuantity);
        } else {
            merchantProductDO.setQuantity(merchantProductDO.getQuantity() + reqQuantity);
        }

        merchantProductDAO.save(merchantProductDO);
        return merchantConverter.toDTO(merchantProductDO);
    }

    public MerchantProductDTO findProductBy(final Long merchantId, final String sku) {
        final MerchantProductDO merchantProductDO = merchantProductDAO.findByMerchantIdAndSkuAndStatus(merchantId, sku, CommonStatusEnum.ACTIVE).orElseThrow(MerchantErrorEnum.PRODUCT_NOT_FOUND::exception);
        return merchantConverter.toDTO(merchantProductDO);
    }

    @Transactional(rollbackOn = Exception.class)
    public void placeOrder(final Long merchantId, final Long productId, final BigDecimal purchasePrice, final String currency, final Integer quantity) {
        final MerchantAccountDO merchantAccountDO = merchantAccountDAO.lockByMerchantIdAndCurrency(merchantId, currency).orElseThrow(MerchantErrorEnum.MERCHANT_ACCOUNT_NOT_FOUND::exception);
        merchantAccountDO.setBalance(DecimalUtils.add(merchantAccountDO.getBalance(), purchasePrice));
        merchantAccountDAO.save(merchantAccountDO);

        final Long productUpdateCount = merchantProductDAO.subtractQuantity(productId, quantity);
        if (productUpdateCount < 1) {
            throw MerchantErrorEnum.MERCHANT_PRODUCT_QUANTITY_FAILED.exception();
        }

        merchantPurchaseLogDAO.save(
            MerchantPurchaseLogDO.builder()
                .merchantId(merchantId)
                .productId(productId)
                .soldQuantity(quantity)
                .purchasePrice(purchasePrice)
                .currency(currency)
                .build()
        );
    }
}
