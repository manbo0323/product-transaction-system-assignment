package com.manbo.merchant.controller.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.manbo.common.util.dto.ResponseDTO;
import jakarta.annotation.Resource;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.number.BigDecimalRandomizer;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

/**
 * @author manboyu
 */
public abstract class BaseControllerTest {

    @Resource
    protected ObjectMapper objectMapper;

    protected EasyRandom generator;
    protected EasyRandomParameters generatorParameter;

    private DefaultResourceLoader resourceLoader;

    protected void setUp() {
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.configure(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES, Boolean.TRUE);
        this.resourceLoader = new DefaultResourceLoader();
        this.generatorParameter = new EasyRandomParameters()
            .randomize(BigDecimal.class, new BigDecimalRandomizer(Integer.valueOf(2)))
            .collectionSizeRange(1, 1);
        this.generator = new EasyRandom(generatorParameter);
    }

    protected JsonNode retrieveResponseData(final String json) throws JsonProcessingException {
        final ResponseDTO<JsonNode> response = objectMapper.readValue(json, new TypeReference<>() {
        });
        final JsonNode data = response.getData();
        assertThat(data.getNodeType()).isNotEqualTo(JsonNodeType.NULL);
        return data;
    }

    protected InputStream inputStream(final String path) throws IOException {
        return resourceLoader.getResource(CLASSPATH_URL_PREFIX + path).getInputStream();
    }
}
