package com.warren.clob;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping (path = "/hello")
    public String helloworld(){
        return "Hello World!";
    }
}
