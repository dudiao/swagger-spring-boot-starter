package com.github.dudiao.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author songyinyin (https://github.com/dudiao)
 * @date 2020/10/28 下午 11:01
 */
@Data
@ConfigurationProperties("springfox.documentation.swagger")
public class Swagger3Properties extends SwaggerProperties{
}
