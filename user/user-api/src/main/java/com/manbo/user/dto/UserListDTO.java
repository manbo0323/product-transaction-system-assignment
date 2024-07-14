package com.manbo.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author manboyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserListDTO extends UserDTO {

    private List<UserAccountDTO> accounts;
}
