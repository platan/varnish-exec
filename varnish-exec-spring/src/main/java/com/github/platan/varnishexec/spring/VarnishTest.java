package com.github.platan.varnishexec.spring;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test class annotation that is used to start varnish for tests.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VarnishTest {

    /**
     * Set a port to {@code 0} to trigger listening on a random port.
     * <p>
     * Default is {@code localhost:10080}.
     */
    HostAndPort address() default @VarnishTest.HostAndPort(host = "localhost", port = 10080);

    /**
     * Only one of "backend" or "config file" can be specified.
     */
    HostAndPort backend() default @HostAndPort(host = "", port = -1);

    /**
     * Only one of "backend" or "config file" can be specified.
     */
    String configFile() default "";

    HostAndPort managementAddress() default @HostAndPort(host = "", port = -1);

    String storage() default "";

    String name() default "";

    boolean randomName() default false;

    String varnishdCommand() default "";

    /**
     * Defines host and port of {@link VarnishTest#address()}, {@link VarnishTest#backend()} or {@link VarnishTest#managementAddress()}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface HostAndPort {
        String host();

        int port();
    }

}

