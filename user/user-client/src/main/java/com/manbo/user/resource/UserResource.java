package com.manbo.user.resource;

import com.manbo.common.util.dto.PageDTO;
import com.manbo.common.util.dto.QueryDTO;
import com.manbo.user.dto.PurchaseReqDTO;
import com.manbo.user.dto.UserListDTO;
import com.manbo.user.dto.UserPurchaseDTO;
import com.manbo.user.dto.UserRechargeDTO;
import com.manbo.user.dto.UserRechargeReqDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author manboyu
 */
public interface UserResource {

    @PostMapping("/users/list")
    PageDTO<UserListDTO> listUser(@RequestBody QueryDTO queryDTO);

    @PutMapping("/users/{id}/recharge")
    UserRechargeDTO recharge(@PathVariable("id") Long id, @RequestBody UserRechargeReqDTO reqDTO);

    @PostMapping("/users/{id}/purchase")
    UserPurchaseDTO purchase(@PathVariable("id") Long id, @RequestBody PurchaseReqDTO reqDTO);
}
