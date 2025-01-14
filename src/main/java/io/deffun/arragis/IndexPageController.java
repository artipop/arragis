package io.deffun.arragis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexPageController {
    @GetMapping
    public String index() {
        return "Hello World!";
    }
}
