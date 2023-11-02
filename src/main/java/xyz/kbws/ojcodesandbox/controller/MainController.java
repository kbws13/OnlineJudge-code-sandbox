package xyz.kbws.ojcodesandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kbws
 * @date 2023/11/2
 * @description:
 */
@RestController("/")
public class MainController {

    @GetMapping("/health")
    public String healthCheck(){
        return "ok";
    }
}
