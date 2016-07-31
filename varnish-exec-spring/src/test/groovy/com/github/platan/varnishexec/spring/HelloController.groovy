package com.github.platan.varnishexec.spring

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @RequestMapping('/')
    String index() {
        'Hello World!'
    }

}
