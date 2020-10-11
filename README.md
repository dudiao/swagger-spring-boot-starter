swagger 3.x 版本与 SpringBoot 整合，简化原生代码配置。

# 简介
Swagger3.0推出了官方starter`springfox-boot-starter`，`springfox.documentation.enabled`配置，可以一键关掉它，
`springfox.documentation.swagger-ui.enabled`参数，可以控制ui的展示，等等。

但用惯了 https://github.com/SpringForAll/spring-boot-starter-swagger （以下简称`spring-boot-starter-swagger`）的starter后，直接使用官方的还需要写一部分代码，来配置。所以，本项目在`springfox-boot-starter`上进行了增强，
以`spring-boot-starter-swagger`为基础，进行了二次开发，配置项大多和`spring-boot-starter-swagger`一样，请放心使用。

## 地址
- Github：https://github.com/dudiao/swagger-spring-boot-starter
- Gitee: https://gitee.com/songyinyin/swagger-spring-boot-starter

## swagger3.x 和 2.x 的区别
- swagger页面地址变更为：`http://localhost:8080/swagger-ui/` （是的，不是swagger-ui.html了，**后边斜杠也要加上**）；
- 不需要在主类上加`@EnableSwagger2`注解，零配置；
- 精简了依赖，比如`guava`

## 和`spring-boot-starter-swagger`的差异
