package com.github.dudiao.swagger.test.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * @author songyinyin
 * @date 2020/11/8 下午 04:56
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private List<Docket> docketList;

    @GetMapping("/login/test")
    public void login() {
        log.info("登录接口");
        log.info("登录接口：{}", docketList);
    }
}
