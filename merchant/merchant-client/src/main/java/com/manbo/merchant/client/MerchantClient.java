package com.manbo.merchant.client;

import com.manbo.common.feign.CommonFeignConfig;
import com.manbo.common.feign.CommonFeignDecoderConfig;
import com.manbo.merchant.resource.MerchantResource;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author manboyu
 */
@FeignClient(value = "merchant-client", url = "${app.merchant.endpoint}", configuration = {CommonFeignDecoderConfig.class, CommonFeignConfig.class})
public interface MerchantClient extends MerchantResource {

}
