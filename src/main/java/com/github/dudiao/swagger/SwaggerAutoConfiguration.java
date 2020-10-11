package com.github.dudiao.swagger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * swagger3 自动装配
 *
 * @author songyinyin (https://github.com/dudiao)
 * @date 2020/10/10 上午 12:11
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration implements BeanFactoryAware {

    @Value("${springfox.documentation.swagger.v2.use-model-v3:true}")
    private Boolean useModelV3;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    public UiConfiguration uiConfiguration(SwaggerProperties swaggerProperties) {
        return UiConfigurationBuilder.builder()
                .deepLinking(swaggerProperties.getUiConfig().getDeepLinking())
                .defaultModelExpandDepth(swaggerProperties.getUiConfig().getDefaultModelExpandDepth())
                .defaultModelRendering(swaggerProperties.getUiConfig().getDefaultModelRendering())
                .defaultModelsExpandDepth(swaggerProperties.getUiConfig().getDefaultModelsExpandDepth())
                .displayOperationId(swaggerProperties.getUiConfig().getDisplayOperationId())
                .displayRequestDuration(swaggerProperties.getUiConfig().getDisplayRequestDuration())
                .docExpansion(swaggerProperties.getUiConfig().getDocExpansion())
                .maxDisplayedTags(swaggerProperties.getUiConfig().getMaxDisplayedTags())
                .operationsSorter(swaggerProperties.getUiConfig().getOperationsSorter())
                .showExtensions(swaggerProperties.getUiConfig().getShowExtensions())
                .tagsSorter(swaggerProperties.getUiConfig().getTagsSorter())
                .validatorUrl(swaggerProperties.getUiConfig().getValidatorUrl())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(UiConfiguration.class)
    @ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
    public List<Docket> createRestApi(SwaggerProperties swaggerProperties) {
        // 没有分组
        if (swaggerProperties.getDocket().size() == 0) {
            return defaultDocket(swaggerProperties);
        }
        // 分组创建
        return customizeDocket(swaggerProperties);
    }

    /**
     * 没有分组，创建默认 Docket
     *
     * @param swaggerProperties 配置项
     * @return
     */
    private List<Docket> defaultDocket(SwaggerProperties swaggerProperties) {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .contact(new Contact(swaggerProperties.getContact().getName(),
                        swaggerProperties.getContact().getUrl(),
                        swaggerProperties.getContact().getEmail()))
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .build();

        Docket docketForBuilder = new Docket(getDocumentationType())
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo)
                .securityContexts(Collections.singletonList(securityContext(swaggerProperties.getAuthorization())))
                .globalRequestParameters(buildRequestParameters(
                        swaggerProperties.getGlobalOperationParameters()));

        setSecuritySchemes(swaggerProperties, docketForBuilder);

        // 全局响应消息
        if (!swaggerProperties.getApplyDefaultResponseMessages()) {
            buildGlobalResponseMessage(swaggerProperties, docketForBuilder);
        }

        Docket docket = docketForBuilder.select()
                .apis(apisPredicate(swaggerProperties.getBasePackage()))
                .paths(pathsPredicate(swaggerProperties.getBasePath(), swaggerProperties.getExcludePath())).build();

        /* ignoredParameterTypes **/
        Class<?>[] array = new Class[swaggerProperties.getIgnoredParameterTypes().size()];
        Class<?>[] ignoredParameterTypes = swaggerProperties.getIgnoredParameterTypes().toArray(array);
        docket.ignoredParameterTypes(ignoredParameterTypes);

        configurableBeanFactory.registerSingleton("defaultDocket", docket);
        return Collections.singletonList(docket);
    }

    /**
     * 多分组情况
     *
     * @param swaggerProperties 配置项
     * @return
     */
    private List<Docket> customizeDocket(SwaggerProperties swaggerProperties) {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        List<Docket> docketList = new LinkedList<>();

        for (String groupName : swaggerProperties.getDocket().keySet()) {
            SwaggerProperties.DocketInfo docketInfo = swaggerProperties.getDocket().get(groupName);

            ApiInfo apiInfo = new ApiInfoBuilder()
                    .title(docketInfo.getTitle().isEmpty() ? swaggerProperties.getTitle() : docketInfo.getTitle())
                    .description(docketInfo.getDescription().isEmpty() ? swaggerProperties.getDescription() : docketInfo.getDescription())
                    .version(docketInfo.getVersion().isEmpty() ? swaggerProperties.getVersion() : docketInfo.getVersion())
                    .license(docketInfo.getLicense().isEmpty() ? swaggerProperties.getLicense() : docketInfo.getLicense())
                    .licenseUrl(docketInfo.getLicenseUrl().isEmpty() ? swaggerProperties.getLicenseUrl() : docketInfo.getLicenseUrl())
                    .contact(
                            new Contact(
                                    docketInfo.getContact().getName().isEmpty() ? swaggerProperties.getContact().getName() : docketInfo.getContact().getName(),
                                    docketInfo.getContact().getUrl().isEmpty() ? swaggerProperties.getContact().getUrl() : docketInfo.getContact().getUrl(),
                                    docketInfo.getContact().getEmail().isEmpty() ? swaggerProperties.getContact().getEmail() : docketInfo.getContact().getEmail()
                            )
                    )
                    .termsOfServiceUrl(docketInfo.getTermsOfServiceUrl().isEmpty() ? swaggerProperties.getTermsOfServiceUrl() : docketInfo.getTermsOfServiceUrl())
                    .build();

            Docket docketForBuilder = new Docket(getDocumentationType())
                    .host(swaggerProperties.getHost())
                    .apiInfo(apiInfo)
                    .securityContexts(Collections.singletonList(securityContext(swaggerProperties.getAuthorization())))
                    .globalRequestParameters(assemblyRequestParameters(swaggerProperties.getGlobalOperationParameters(),
                            docketInfo.getGlobalOperationParameters()));

            setSecuritySchemes(swaggerProperties, docketForBuilder);

            // 全局响应消息
            if (!swaggerProperties.getApplyDefaultResponseMessages()) {
                buildGlobalResponseMessage(swaggerProperties, docketForBuilder);
            }

            Docket docket = docketForBuilder.groupName(groupName)
                    .select()
                    .apis(apisPredicate(docketInfo.getBasePackage()))
                    .paths(pathsPredicate(docketInfo.getBasePath(), docketInfo.getExcludePath()))
                    .build();

            /* ignoredParameterTypes **/
            Class<?>[] array = new Class[docketInfo.getIgnoredParameterTypes().size()];
            Class<?>[] ignoredParameterTypes = docketInfo.getIgnoredParameterTypes().toArray(array);
            docket.ignoredParameterTypes(ignoredParameterTypes);

            configurableBeanFactory.registerSingleton(groupName, docket);
            docketList.add(docket);
        }
        return docketList;
    }

    /**
     * 获取文档版本
     *
     * @return 文档版本
     */
    private DocumentationType getDocumentationType() {
        if (useModelV3) {
            return DocumentationType.OAS_30;
        }
        return DocumentationType.SWAGGER_2;
    }

    /**
     * 设置鉴权对象
     *
     * @param swaggerProperties swagger 配置文件
     * @param docketForBuilder  待构建的 docket
     */
    private void setSecuritySchemes(SwaggerProperties swaggerProperties, Docket docketForBuilder) {
        if (SwaggerSecurityScheme.BASIC_AUTH.equals(swaggerProperties.getAuthorization().getType())) {
            // 配置基于 BasicAuth 的鉴权对象
            docketForBuilder.securitySchemes(Collections.singletonList(new BasicAuth(swaggerProperties.getAuthorization().getName())));
        } else if (SwaggerSecurityScheme.API_KEY.equals(swaggerProperties.getAuthorization().getType())) {
            // 配置基于 ApiKey 的鉴权对象
            docketForBuilder.securitySchemes(Collections.singletonList(new ApiKey(swaggerProperties.getAuthorization().getName(),
                    swaggerProperties.getAuthorization().getKeyName(),
                    ApiKeyVehicle.HEADER.getValue())));
        }
    }

    /**
     * 基础包扫描，不支持正则表达式
     *
     * @param basePackages 基础包前缀
     * @return RequestHandler predicate
     */
    private Predicate<RequestHandler> apisPredicate(List<String> basePackages) {
        return input -> {
            for (String basePackage : basePackages) {
                if (RequestHandlerSelectors.basePackage(basePackage).test(input)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 路径选择
     *
     * @param basePaths    基础路径集合
     * @param excludePaths 排除的路径集合
     * @return path predicate
     */
    private Predicate<String> pathsPredicate(List<String> basePaths, List<String> excludePaths) {
        return input -> {
            // exclude-path处理
            for (String excludePath : excludePaths) {
                if (PathSelectors.ant(excludePath).test(input)) {
                    return false;
                }
            }
            // base-path处理：当没有配置任何path的时候，解析/**
            if (basePaths.isEmpty()) {
                basePaths.add("/**");
            }
            for (String basePath : basePaths) {
                if (PathSelectors.ant(basePath).test(input)) {
                    return true;
                }
            }
            return true;
        };
    }

    /**
     * 配置默认的全局鉴权策略的开关，以及通过正则表达式进行匹配；默认 ^.*$ 匹配所有URL
     * 其中 securityReferences 为配置启用的鉴权策略
     *
     * @return
     */
    private SecurityContext securityContext(SwaggerProperties.Authorization authorization) {

        return SecurityContext.builder()
                .securityReferences(defaultAuth(authorization.getName()))
                .forPaths(PathSelectors.regex(authorization.getAuthRegex()))
                .build();
    }

    /**
     * 配置默认的全局鉴权策略；其中返回的 SecurityReference 中，reference 即为ApiKey对象里面的name，保持一致才能开启全局鉴权
     *
     * @return
     */
    private List<SecurityReference> defaultAuth(String authorizationName) {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(SecurityReference.builder()
                .reference(authorizationName)
                .scopes(authorizationScopes).build());
    }

    /**
     * 构建全局请求参数
     *
     * @param globalOperationParameters 全局参数配置项
     */
    private List<RequestParameter> buildRequestParameters(
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters) {
        List<RequestParameter> parameters = new ArrayList<>();

        if (Objects.isNull(globalOperationParameters)) {
            return parameters;
        }
        for (SwaggerProperties.GlobalOperationParameter globalOperationParameter : globalOperationParameters) {
            parameters.add(new RequestParameterBuilder()
                    .name(globalOperationParameter.getName())
                    .description(globalOperationParameter.getDescription())
                    .query(sb -> sb.model(m -> m.scalarModel(globalOperationParameter.getScalarType())))
                    .in(globalOperationParameter.getParameterType())
                    .required(Boolean.parseBoolean(globalOperationParameter.getRequired()))
                    .build());
        }
        return parameters;
    }

    /**
     * 局部参数按照name覆盖局部参数
     *
     * @param globalOperationParameters 全局参数配置项
     * @param docketOperationParameters 分组参数配置项
     */
    private List<RequestParameter> assemblyRequestParameters(
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters,
            List<SwaggerProperties.GlobalOperationParameter> docketOperationParameters) {

        if (Objects.isNull(docketOperationParameters) || docketOperationParameters.isEmpty()) {
            return buildRequestParameters(globalOperationParameters);
        }

        Set<String> docketNames = docketOperationParameters.stream()
                .map(SwaggerProperties.GlobalOperationParameter::getName)
                .collect(Collectors.toSet());

        List<SwaggerProperties.GlobalOperationParameter> resultOperationParameters = new ArrayList<>();

        if (Objects.nonNull(globalOperationParameters)) {
            for (SwaggerProperties.GlobalOperationParameter parameter : globalOperationParameters) {
                if (!docketNames.contains(parameter.getName())) {
                    resultOperationParameters.add(parameter);
                }
            }
        }

        resultOperationParameters.addAll(docketOperationParameters);
        return buildRequestParameters(resultOperationParameters);
    }

    /**
     * 设置全局响应消息
     *
     * @param swaggerProperties swaggerProperties 支持 POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE
     * @param docketForBuilder  swagger docket builder
     */
    private void buildGlobalResponseMessage(SwaggerProperties swaggerProperties, Docket docketForBuilder) {

        SwaggerProperties.GlobalResponseMessage globalResponseMessages =
                swaggerProperties.getGlobalResponseMessage();

        /* POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE 响应消息体 **/
        List<Response> postResponseMessages = getResponseMessageList(globalResponseMessages.getPost());
        List<Response> getResponseMessages = getResponseMessageList(globalResponseMessages.getGet());
        List<Response> putResponseMessages = getResponseMessageList(globalResponseMessages.getPut());
        List<Response> patchResponseMessages = getResponseMessageList(globalResponseMessages.getPatch());
        List<Response> deleteResponseMessages = getResponseMessageList(globalResponseMessages.getDelete());
        List<Response> headResponseMessages = getResponseMessageList(globalResponseMessages.getHead());
        List<Response> optionsResponseMessages = getResponseMessageList(globalResponseMessages.getOptions());
        List<Response> trackResponseMessages = getResponseMessageList(globalResponseMessages.getTrace());

        docketForBuilder.useDefaultResponseMessages(swaggerProperties.getApplyDefaultResponseMessages())
                .globalResponses(HttpMethod.POST, postResponseMessages)
                .globalResponses(HttpMethod.GET, getResponseMessages)
                .globalResponses(HttpMethod.PUT, putResponseMessages)
                .globalResponses(HttpMethod.PATCH, patchResponseMessages)
                .globalResponses(HttpMethod.DELETE, deleteResponseMessages)
                .globalResponses(HttpMethod.HEAD, headResponseMessages)
                .globalResponses(HttpMethod.OPTIONS, optionsResponseMessages)
                .globalResponses(HttpMethod.TRACE, trackResponseMessages);
    }

    /**
     * 获取返回消息体列表
     *
     * @param globalResponseMessageBodyList 全局Code消息返回集合
     * @return
     */
    private List<Response> getResponseMessageList
    (List<SwaggerProperties.GlobalResponseMessageBody> globalResponseMessageBodyList) {

        List<Response> responseMessages = new ArrayList<>();
        for (SwaggerProperties.GlobalResponseMessageBody globalResponseMessageBody : globalResponseMessageBodyList) {
            ResponseBuilder responseBuilder = new ResponseBuilder();
            responseBuilder.code(globalResponseMessageBody.getCode()).description(globalResponseMessageBody.getMessage());

            if (!StringUtils.isEmpty(globalResponseMessageBody.getModelRef())) {
                responseBuilder.vendorExtensions(Collections.singletonList(new ObjectVendorExtension(globalResponseMessageBody.getModelRef())));
            }
            responseMessages.add(responseBuilder.build());
        }

        return responseMessages;
    }
}
