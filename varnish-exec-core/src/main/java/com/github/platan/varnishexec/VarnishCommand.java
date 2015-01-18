package com.github.platan.varnishexec;

import com.github.platan.varnishexec.net.HostAndPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A representation of a {@code varnishd} command arguments.
 */
public final class VarnishCommand {

    private static final String FOREGROUND_PARAM = "-F";

    private final Option<HostAndPort> address;
    private final Option<Optional<HostAndPort>> backend;
    private final Option<Optional<String>> configFile;
    private final Option<Optional<HostAndPort>> managementAddress;
    private final Option<Optional<String>> storage;
    private final Option<String> name;
    private final String varnishdCommand;

    private VarnishCommand(Builder builder) {
        address = new Option<>(builder.address, "-a");
        backend = new Option<>(builder.backend, "-b");
        configFile = new Option<>(builder.configFile, "-f");
        managementAddress = new Option<>(builder.managementAddress, "-T");
        storage = new Option<>(builder.storage, "-s");
        name = new Option<>(builder.name, "-n");
        varnishdCommand = builder.varnishdCommand;
    }

    public String getVarnishdCommand() {
        return varnishdCommand;
    }

    public HostAndPort getAddress() {
        return address.getValue();
    }

    public Optional<HostAndPort> getBackend() {
        return backend.getValue();
    }

    public Optional<String> getConfigFile() {
        return configFile.getValue();
    }

    public Optional<HostAndPort> getManagementAddress() {
        return managementAddress.getValue();
    }

    public Optional<String> getStorage() {
        return storage.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    /**
     * Returns an array containing {@code varnishd} command with arguments. Example:
     * <pre>{@code varnishd -a localhost:10080 -F -f ./src/test/etc/varnish/default.vcl -n /tmp -s malloc,64M -T localhost:2000 }</pre>
     *
     * @return {@code varnishd} command as an array.
     */
    public String[] asArray() {
        List<String> command = new ArrayList<>();
        command.add(varnishdCommand);
        addOption(command, address);
        addOptionalOption(command, backend);
        command.add(FOREGROUND_PARAM);
        addOptionalOption(command, configFile);
        addOption(command, name);
        addOptionalOption(command, storage);
        addOptionalOption(command, managementAddress);
        return command.toArray(new String[command.size()]);
    }

    private <T> void addOptionalOption(List<String> command, Option<Optional<T>> option) {
        if (option.getValue().isPresent()) {
            command.add(option.getName());
            command.add(option.getValue().get().toString());
        }
    }

    private <T> void addOption(List<String> command, Option<T> option) {
        command.add(option.getName());
        command.add(option.getValue().toString());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private static final String DEFAULT_VARNISHD_COMMAND = "varnishd";
        private static final HostAndPort DEFAULT_ADDRESS = new HostAndPort("localhost", 10080);
        private static final String DEFAULT_NAME = "/tmp";

        private String varnishdCommand = DEFAULT_VARNISHD_COMMAND;
        private HostAndPort address = DEFAULT_ADDRESS;
        private Optional<HostAndPort> backend = Optional.empty();
        private Optional<String> configFile = Optional.empty();
        private Optional<HostAndPort> managementAddress = Optional.empty();
        private Optional<String> storage = Optional.empty();
        private String name = DEFAULT_NAME;

        public Builder() {
        }

        public Builder withAddress(String host, int port) {
            this.address = new HostAndPort(host, port);
            return this;
        }

        public Builder withBackend(String host, int port) {
            this.backend = Optional.of(new HostAndPort(host, port));
            return this;
        }

        public Builder withConfigFile(String configFile) {
            this.configFile = Optional.of(configFile);
            return this;
        }

        public Builder withManagementAddress(String host, int port) {
            this.managementAddress = Optional.of(new HostAndPort(host, port));
            return this;
        }

        public Builder withStorage(String storage) {
            this.storage = Optional.of(storage);
            return this;
        }

        public Builder withName(String name) {
            checkNotNull(name, "name");
            this.name = name;
            return this;
        }

        public Builder withVarnishdCommand(String varnishdCommand) {
            checkNotNull(varnishdCommand, "varnishCommand");
            this.varnishdCommand = varnishdCommand;
            return this;
        }

        private static void checkNotNull(String referenceValue, final String referenceName) {
            if (referenceValue == null) {
                throw new NullPointerException(referenceName + " cannot be null!");
            }
        }

        public VarnishCommand build() {
            VarnishCommand varnishCommand = new VarnishCommand(this);
            checkState(varnishCommand);
            return varnishCommand;
        }

        private void checkState(VarnishCommand varnishCommand) {
            if (varnishCommand.backend.getValue().isPresent() && varnishCommand.configFile.getValue().isPresent()) {
                throw new IllegalStateException("Only one of \"backend\" or \"config file\" can be specified");
            }
        }

    }

    private static final class Option<T> {

        private final T value;
        private final String name;

        public Option(T value, String name) {
            this.value = value;
            this.name = name;
        }

        public T getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
