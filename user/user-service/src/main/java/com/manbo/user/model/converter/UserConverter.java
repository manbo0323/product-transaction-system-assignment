package com.manbo.user.model.converter;

import com.manbo.user.dto.UserDTO;
import com.manbo.user.dto.UserRechargeDTO;
import com.manbo.user.dto.UserRechargeReqDTO;
import com.manbo.user.model.UserAccountDO;
import com.manbo.user.model.UserDO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Manbo
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true))
public interface UserConverter {

    UserDTO toDTO(UserDO data);

    UserRechargeDTO toDTO(UserAccountDO data, String name, String email);
}
