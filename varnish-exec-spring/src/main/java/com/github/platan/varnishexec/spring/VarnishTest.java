package com.github.platan.varnishexec.spring;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VarnishTest {
    HostAndPort address() default @VarnishTest.HostAndPort(host = "localhost", port = 10080);

    HostAndPort backend() default @HostAndPort(host = "", port = -1);

    String configFile() default "";

    HostAndPort managementAddress() default @HostAndPort(host = "", port = -1);

    String storage() default "";

    String name() default "";

    boolean randomName() default false;

    String varnishdCommand() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface HostAndPort {
        String host();

        int port();
    }

}

