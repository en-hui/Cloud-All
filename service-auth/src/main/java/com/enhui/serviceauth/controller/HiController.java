package com.enhui.serviceauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡恩会
 * @date 2020/12/15 0:07
 */
@RestController
public class HiController {
    @GetMapping("/hi")
    public String getHi() {
        return "hi";
    }
}
