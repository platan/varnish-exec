package com.github.platan.varnishexec.spring

import static org.springframework.boot.SpringApplication.run

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application {

    static void main(String[] args) {
        run(Application, args)
    }

}
