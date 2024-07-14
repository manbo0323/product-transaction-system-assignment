package com.manbo.user.service;

import com.manbo.common.service.BaseService;
import com.manbo.common.util.dto.BaseDTO;
import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.common.util.enums.CommonStatusEnum;
import com.manbo.common.util.utils.CollectionUtils;
import com.manbo.common.util.utils.DecimalUtils;
import com.manbo.merchant.client.MerchantClient;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.user.dao.UserAccountDAO;
import com.manbo.user.dao.UserDAO;
import com.manbo.user.dto.PurchaseReqDTO;
import com.manbo.user.dto.UserAccountDTO;
import com.manbo.user.dto.UserListDTO;
import com.manbo.user.dto.UserPurchaseDTO;
import com.manbo.user.dto.UserRechargeDTO;
import com.manbo.user.dto.UserRechargeReqDTO;
import com.manbo.user.exception.UserErrorEnum;
import com.manbo.user.model.UserAccountDO;
import com.manbo.user.model.UserDO;
import com.manbo.user.model.converter.UserConverter;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author manboyu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends BaseService {

    private final UserDAO userDAO;
    private final UserAccountDAO userAccountDAO;
    private final UserConverter userConverter;

    private final MerchantClient merchantClient;

    public PageDTO<UserListDTO> listUser(final QueryDTO queryDTO) {
        final PageDTO<UserListDTO> pageData = convert(userDAO.findUsers(queryDTO));
        final List<UserListDTO> users = pageData.getList();
        final List<Long> userIds = CollectionUtils.streamOf(users).map(BaseDTO::getId).toList();
        final Map<Long, List<UserAccountDTO>> userAccountGroup = CollectionUtils.streamOf(userAccountDAO.findByUserIds(userIds))
            .collect(Collectors.groupingBy(UserAccountDTO::getUserId));
        users.forEach(user -> user.setAccounts(userAccountGroup.get(user.getId())));
        return pageData;
    }

    @Transactional(rollbackOn = Exception.class)
    public UserRechargeDTO recharge(final Long id, final UserRechargeReqDTO reqDTO) {
        final UserDO userDO = userDAO.findById(id).orElseThrow(UserErrorEnum.USER_NOT_FOUND::exception);
        if (Objects.equals(CommonStatusEnum.INACTIVE, userDO.getStatus())) {
            throw UserErrorEnum.USER_INACTIVE.exception();
        }

        final UserAccountDO userAccountDO = userAccountDAO.lockByUserIdAndCurrency(userDO.getId(), reqDTO.getCurrency()).orElseThrow(UserErrorEnum.USER_CURRENCY_NOT_FOUND::exception);
        userAccountDO.setBalance(DecimalUtils.add(userAccountDO.getBalance(), reqDTO.getAmount()));
        userAccountDAO.save(userAccountDO);
        return userConverter.toDTO(userAccountDO, userDO.getName(), userDO.getEmail());
    }

    @Transactional(rollbackOn = Exception.class)
    public UserPurchaseDTO purchase(final Long id, @Valid final PurchaseReqDTO reqDTO) {
        final UserDO userDO = userDAO.findById(id).orElseThrow(UserErrorEnum.USER_NOT_FOUND::exception);
        if (Objects.equals(CommonStatusEnum.INACTIVE, userDO.getStatus())) {
            throw UserErrorEnum.USER_INACTIVE.exception();
        }

        final Integer reqQuantity = reqDTO.getQuantity();
        final MerchantProductDTO productDTO = merchantClient.findProductBy(reqDTO.getMerchantId(), reqDTO.getSku());
        if (reqQuantity > productDTO.getQuantity()) {
            throw UserErrorEnum.INSUFFICIENT_PRODUCT_INVENTORY.exception();
        }

        final BigDecimal purchasePrice = DecimalUtils.multiply(productDTO.getPrice(), reqQuantity);
        final UserAccountDO userAccountDO = userAccountDAO.lockByUserIdAndCurrency(userDO.getId(), reqDTO.getCurrency()).orElseThrow(UserErrorEnum.USER_CURRENCY_NOT_FOUND::exception);
        if (DecimalUtils.gt(purchasePrice, userAccountDO.getBalance())) {
            throw UserErrorEnum.INSUFFICIENT_BALANCE.exception();
        }

        userAccountDO.setBalance(DecimalUtils.subtract(userAccountDO.getBalance(), purchasePrice));
        userAccountDAO.save(userAccountDO);

        merchantClient.placeOrder(reqDTO.getMerchantId(), productDTO.getId(), purchasePrice, reqDTO.getCurrency(), reqQuantity);

        return UserPurchaseDTO.builder()
            .userId(userDO.getId())
            .name(userDO.getName())
            .email(userDO.getEmail())
            .currency(userAccountDO.getCurrency())
            .balance(userAccountDO.getBalance())
            .purchasePrice(purchasePrice)
            .productName(productDTO.getName())
            .build();
    }
}
