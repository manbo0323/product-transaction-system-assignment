package com.manbo.user.dto;

import com.manbo.common.util.dto.BaseDTO;
import com.manbo.common.util.enums.CommonStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author manboyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {

    private String name;
    private String email;
    private CommonStatusEnum status;
}
