package com.manbo.merchant.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author manboyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantListDTO extends MerchantDTO {

    private List<MerchantAccountDTO> accounts;
}
