package com.github.platan.varnishexec.spring;

import com.github.platan.varnishexec.VarnishCommand;
import com.github.platan.varnishexec.VarnishExecs;
import com.github.platan.varnishexec.VarnishProcess;
import com.github.platan.varnishexec.spring.VarnishTest.HostAndPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.createTempFile;


public class VarnishTestExecutionListener extends AbstractTestExecutionListener {

    private static final Random RANDOM = new Random();
    private static final String VARNISH_PROPERTY_SOURCE = "varnish";
    private VarnishProcess varnishProcess;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);
        VarnishTest varnishTest = testContext.getTestClass().getAnnotation(VarnishTest.class);
        if (varnishTest == null) {
            return;
        }

        String applicationPort = getApplicationPort(testContext.getApplicationContext().getEnvironment());
        VarnishCommand.Builder builder = buildVarnishCommand(varnishTest, applicationPort);
        VarnishCommand varnishCommand = builder.build();
        setPortProperty(testContext.getApplicationContext(), "local.varnish.port", varnishCommand.getAddress().getPort());
        varnishProcess = VarnishExecs.start(varnishCommand);
    }

    private VarnishCommand.Builder buildVarnishCommand(VarnishTest varnishTest, String applicationPort) throws IOException {
        VarnishCommand.Builder builder = VarnishCommand.newBuilder();
        HostAndPort address = varnishTest.address();
        builder.withAddress(address.host(), preparePort(address.port()));
        HostAndPort managementAddress = varnishTest.managementAddress();
        if (hostIsDefined(managementAddress)) {
            builder.withManagementAddress(managementAddress.host(), managementAddress.port());
        }
        HostAndPort backend = varnishTest.backend();
        if (hostIsDefined(backend)) {
            builder.withBackend(backend.host(), getBackendPort(backend, applicationPort));
        }
        String storage = varnishTest.storage();
        if (!storage.isEmpty()) {
            builder.withStorage(storage);
        }
        String name;
        if (varnishTest.randomName()) {
            name = createTempDirectory("varnish").toAbsolutePath().toString();
        } else {
            name = varnishTest.name();
        }
        if (!name.isEmpty()) {
            builder.withName(name);
        }
        String varnishdCommand = varnishTest.varnishdCommand();
        if (!varnishdCommand.isEmpty()) {
            builder.withVarnishdCommand(varnishdCommand);
        }
        String vclScript = varnishTest.configFile();
        if (!vclScript.isEmpty()) {
            String vclFile = createVclScript(vclScript, applicationPort);
            builder.withConfigFile(vclFile);
        }
        return builder;
    }

    private boolean hostIsDefined(HostAndPort address) {
        return !address.host().isEmpty() && address.port() != -1;
    }

    private int preparePort(int port) {
        return port == 0 ? getRandomPort() : port;
    }

    private int getRandomPort() {
        return RANDOM.nextInt(65535) + 1;
    }

    private void setPortProperty(ApplicationContext context, String propertyName, int port) {
        if (context instanceof ConfigurableApplicationContext) {
            MutablePropertySources sources = ((ConfigurableApplicationContext) context).getEnvironment().getPropertySources();
            Map<String, Object> map;
            if (sources.contains(VARNISH_PROPERTY_SOURCE)) {
                map = (Map<String, Object>) sources.get(VARNISH_PROPERTY_SOURCE).getSource();
            } else {
                map = new HashMap<>();
                MapPropertySource source = new MapPropertySource(VARNISH_PROPERTY_SOURCE, map);
                sources.addFirst(source);
            }
            map.put(propertyName, port);
        }
        if (context.getParent() != null) {
            setPortProperty(context.getParent(), propertyName, port);
        }
    }

    private String getApplicationPort(Environment environment) {
        return environment.getProperty("local.server.port");
    }

    private int getBackendPort(HostAndPort backend, String applicationPort) {
        return backend.port() == 0 ? parseInt(applicationPort) : backend.port();
    }

    private String createVclScript(String vclScript, String applicationPort) throws IOException {
        String vclTemplate = readResource(vclScript);
        String vclContentWithPort = vclTemplate.replaceFirst("@local.port@", applicationPort);
        Path vclPath = createTempFile("test", "vcl");
        Files.write(vclPath, vclContentWithPort.getBytes(UTF_8));
        return vclPath.toAbsolutePath().toString();
    }

    private String readResource(String file) throws IOException {
        Path filePath = Paths.get(toUri(getClass().getClassLoader().getResource(file)));
        return new String(Files.readAllBytes(filePath));
    }

    private URI toUri(URL resource) {
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        if (varnishProcess != null) {
            varnishProcess.kill();
        }
        super.afterTestClass(testContext);
    }

    @Override
    public int getOrder() {
        return 1900;
    }

}