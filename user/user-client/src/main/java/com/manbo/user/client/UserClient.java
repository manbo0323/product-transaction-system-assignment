package com.manbo.user.client;

import com.manbo.common.feign.CommonFeignConfig;
import com.manbo.common.feign.CommonFeignDecoderConfig;
import com.manbo.user.resource.UserResource;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author manboyu
 */
@FeignClient(value = "user-client", url = "${app.user.endpoint}", configuration = {CommonFeignDecoderConfig.class, CommonFeignConfig.class})
public interface UserClient extends UserResource {

}
