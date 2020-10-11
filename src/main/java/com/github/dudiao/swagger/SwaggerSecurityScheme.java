package com.github.dudiao.swagger;

/**
 * @author songyinyin (https://github.com/dudiao)
 * @date 2020/10/11 下午 04:59
 */
public enum SwaggerSecurityScheme {
    /**
     * 无
     */
    NONE,
    /**
     * 基于 BasicAuth 的鉴权对象
     */
    BASIC_AUTH,
    /**
     * 基于 ApiKey 的鉴权对象
     */
    API_KEY
}
