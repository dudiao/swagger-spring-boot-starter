package com.github.dudiao.swagger;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyinyin (https://github.com/dudiao)
 * @date 2020/10/10 上午 12:06
 */
@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {

    /**
     * 标题
     **/
    protected String title = "";
    /**
     * 描述
     **/
    protected String description = "";
    /**
     * 版本
     **/
    protected String version = "";
    /**
     * 许可证
     **/
    protected String license = "";
    /**
     * 许可证URL
     **/
    protected String licenseUrl = "";
    /**
     * 服务条款URL
     **/
    protected String termsOfServiceUrl = "";

    /**
     * 忽略的参数类型
     **/
    protected List<Class<?>> ignoredParameterTypes = new ArrayList<>();

    /**
     * 联系人信息
     */
    @NestedConfigurationProperty
    protected Contact contact = new Contact();

    /**
     * swagger会解析的包路径（前缀）集合，不支持正则表达式
     **/
    protected List<String> basePackage = new ArrayList<>();

    /**
     * swagger会解析的url规则
     **/
    protected List<String> basePath = new ArrayList<>();
    /**
     * 在basePath基础上需要排除的url规则
     **/
    protected List<String> excludePath = new ArrayList<>();

    /**
     * 分组文档
     **/
    protected Map<String, DocketInfo> docket = new LinkedHashMap<>();

    /**
     * host信息
     **/
    protected String host = "";

    /**
     * 全局参数配置
     **/
    protected List<GlobalOperationParameter> globalOperationParameters;

    /**
     * 页面功能配置
     **/
    @NestedConfigurationProperty
    protected UiConfig uiConfig = new UiConfig();

    /**
     * 是否使用默认预定义的响应消息 ，默认 true
     **/
    protected Boolean applyDefaultResponseMessages = true;

    /**
     * 全局响应消息
     **/
    @NestedConfigurationProperty
    protected GlobalResponseMessage globalResponseMessage;

    /**
     * 全局统一鉴权配置
     **/
    @NestedConfigurationProperty
    protected Authorization authorization = new Authorization();

    @Data
    @NoArgsConstructor
    public static class GlobalOperationParameter {
        /**
         * 参数名
         **/
        protected String name;

        /**
         * 描述信息
         **/
        protected String description;

        /**
         * 指定参数类型
         **/
        protected ScalarType scalarType;

        /**
         * 参数放在哪个地方:header,query,path,body,form
         **/
        protected String parameterType;

        /**
         * 参数是否必须传
         **/
        protected String required;

    }

    @Data
    @NoArgsConstructor
    public static class DocketInfo {

        /**
         * 标题
         **/
        protected String title = "";
        /**
         * 描述
         **/
        protected String description = "";
        /**
         * 版本
         **/
        protected String version = "";
        /**
         * 许可证
         **/
        protected String license = "";
        /**
         * 许可证URL
         **/
        protected String licenseUrl = "";
        /**
         * 服务条款URL
         **/
        protected String termsOfServiceUrl = "";

        /**
         * 联系人信息
         */
        @NestedConfigurationProperty
        protected Contact contact = new Contact();

        /**
         * swagger会解析的包路径
         **/
        protected List<String> basePackage = new ArrayList<>();

        /**
         * swagger会解析的url规则
         **/
        protected List<String> basePath = new ArrayList<>();
        /**
         * 在basePath基础上需要排除的url规则
         **/
        protected List<String> excludePath = new ArrayList<>();

        protected List<GlobalOperationParameter> globalOperationParameters;

        /**
         * 忽略的参数类型
         **/
        protected List<Class<?>> ignoredParameterTypes = new ArrayList<>();

    }

    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * 联系人
         **/
        protected String name = "";
        /**
         * 联系人url
         **/
        protected String url = "";
        /**
         * 联系人email
         **/
        protected String email = "";

    }

    @Data
    @NoArgsConstructor
    public static class GlobalResponseMessage {

        /**
         * POST 响应消息体
         **/
        List<GlobalResponseMessageBody> post = new ArrayList<>();

        /**
         * GET 响应消息体
         **/
        List<GlobalResponseMessageBody> get = new ArrayList<>();

        /**
         * PUT 响应消息体
         **/
        List<GlobalResponseMessageBody> put = new ArrayList<>();

        /**
         * PATCH 响应消息体
         **/
        List<GlobalResponseMessageBody> patch = new ArrayList<>();

        /**
         * DELETE 响应消息体
         **/
        List<GlobalResponseMessageBody> delete = new ArrayList<>();

        /**
         * HEAD 响应消息体
         **/
        List<GlobalResponseMessageBody> head = new ArrayList<>();

        /**
         * OPTIONS 响应消息体
         **/
        List<GlobalResponseMessageBody> options = new ArrayList<>();

        /**
         * TRACE 响应消息体
         **/
        List<GlobalResponseMessageBody> trace = new ArrayList<>();

    }

    @Data
    @NoArgsConstructor
    public static class GlobalResponseMessageBody {

        /**
         * 响应码
         **/
        protected String code;

        /**
         * 响应消息
         **/
        protected String message;

        /**
         * 响应体
         **/
        protected String modelRef;

    }


    @Data
    @NoArgsConstructor
    public static class UiConfig {


        protected String apiSorter = "alpha";

        /**
         * 是否启用json编辑器
         **/
        protected Boolean jsonEditor = false;
        /**
         * 是否显示请求头信息
         **/
        protected Boolean showRequestHeaders = true;
        /**
         * 支持页面提交的请求类型
         **/
        protected String submitMethods = "get,post,put,delete,patch";
        /**
         * 请求超时时间
         **/
        protected Long requestTimeout = 10000L;

        protected Boolean deepLinking;
        protected Boolean displayOperationId;
        protected Integer defaultModelsExpandDepth;
        protected Integer defaultModelExpandDepth;
        protected ModelRendering defaultModelRendering;

        /**
         * 是否显示请求耗时，默认 true
         */
        protected Boolean displayRequestDuration = true;
        /**
         * 可选 none | list
         */
        protected DocExpansion docExpansion;
        /**
         * Boolean=false OR String
         */
        protected Object filter;
        protected Integer maxDisplayedTags;
        protected OperationsSorter operationsSorter;
        protected Boolean showExtensions;
        protected TagsSorter tagsSorter;

        /**
         * Network
         */
        protected String validatorUrl;
    }

    /**
     * securitySchemes 支持方式之一 ApiKey
     */
    @Data
    @NoArgsConstructor
    public static class Authorization {

        /**
         * 鉴权策略ID，对应 SecurityReferences ID
         */
        protected String name = "Authorization";

        /**
         * 鉴权策略，可选 ApiKey | BasicAuth ，默认ApiKey
         */
        protected SwaggerSecurityScheme type = SwaggerSecurityScheme.API_KEY;

        /**
         * 鉴权传递的Header参数
         */
        protected String keyName = "TOKEN";

        /**
         * 需要开启鉴权URL的正则
         */
        protected String authRegex = "^.*$";
    }
}
