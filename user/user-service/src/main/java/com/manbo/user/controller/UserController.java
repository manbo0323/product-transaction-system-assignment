package com.manbo.user.controller;

import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.user.dto.PurchaseReqDTO;
import com.manbo.user.dto.UserListDTO;
import com.manbo.user.dto.UserPurchaseDTO;
import com.manbo.user.dto.UserRechargeDTO;
import com.manbo.user.dto.UserRechargeReqDTO;
import com.manbo.user.resource.UserResource;
import com.manbo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author manboyu
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserResource {

    private final UserService userService;

    @Override
    @Operation(summary = "User list")
    public PageDTO<UserListDTO> listUser(final QueryDTO queryDTO) {
        return userService.listUser(queryDTO);
    }

    @Override
    @Operation(summary = "Recharge User Account")
    public UserRechargeDTO recharge(final Long id, @Valid final UserRechargeReqDTO reqDTO) {
        return userService.recharge(id, reqDTO);
    }

    @Override
    @Operation(summary = "Purchase Product")
    public UserPurchaseDTO purchase(final Long id, @Valid final PurchaseReqDTO reqDTO) {
        return userService.purchase(id, reqDTO);
    }
}
