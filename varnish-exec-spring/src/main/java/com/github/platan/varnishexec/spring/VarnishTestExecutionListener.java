package com.github.platan.varnishexec.spring;

import com.github.platan.varnishexec.VarnishCommand;
import com.github.platan.varnishexec.VarnishExecs;
import com.github.platan.varnishexec.VarnishProcess;
import com.github.platan.varnishexec.spring.VarnishTest.HostAndPort;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.createTempFile;


public class VarnishTestExecutionListener extends AbstractTestExecutionListener {

    private VarnishProcess varnishProcess;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);
        VarnishTest varnishTest = testContext.getTestClass().getAnnotation(VarnishTest.class);
        if (varnishTest == null) {
            return;
        }

        startVarnish(testContext, varnishTest);
    }

    private void startVarnish(TestContext testContext, VarnishTest varnishTest) throws IOException {
        String vclScript = varnishTest.configFile();
        String applicationPort = getApplicationPort(testContext);
        String vclFile = createVclScript(vclScript, applicationPort);
        VarnishCommand.Builder builder = VarnishCommand.newBuilder();

        HostAndPort address = varnishTest.address();
        if (hostIsDefined(address)) {
            builder.withAddress(address.host(), address.port());
        }
        HostAndPort managementAddress = varnishTest.managementAddress();
        if (hostIsDefined(managementAddress)) {
            builder.withManagementAddress(managementAddress.host(), managementAddress.port());
        }
        HostAndPort backend = varnishTest.backend();
        if (hostIsDefined(backend)) {
            builder.withBackend(backend.host(), backend.port());
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
        VarnishCommand varnishCommand = builder
                .withConfigFile(vclFile)
                .build();
        varnishProcess = VarnishExecs.start(varnishCommand);
    }

    private boolean hostIsDefined(HostAndPort address) {
        return !address.host().isEmpty() && address.port() != -1;
    }

    private String getApplicationPort(TestContext testContext) {
        return testContext.getApplicationContext().getEnvironment().getProperty("local.server.port");
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
}