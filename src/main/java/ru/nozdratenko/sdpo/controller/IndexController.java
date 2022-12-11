package ru.nozdratenko.sdpo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    /**
     * Return main frontend file on start application
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @RequestMapping("/**/{path:[^.]*}")
    public String redirect() {
        return "index.html";
    }

}
