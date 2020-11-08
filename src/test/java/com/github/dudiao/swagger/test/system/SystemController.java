package com.github.dudiao.swagger.test.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author songyinyin
 * @date 2020/11/8 下午 04:57
 */
@Slf4j
@RestController
public class SystemController {

    @GetMapping("/system/test")
    public void system() {
        log.info("系统接口");
    }
}
