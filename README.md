# Varnish Exec
Java library and [JUnit](http://junit.org/) [rule](https://github.com/junit-team/junit/wiki/Rules) for running [Varnish Cache](https://www.varnish-cache.org/) daemon.

- [Modules](#modules)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Customization](#customization)
- [License](#license)

## Modules
- varnish-exec-core - Core module
- varnish-exec-junit - Integration with the JUnit

## Requirements
- JDK 8
- Varnish Cache 3.0 or 4.0

## Installation

Currently Varnish Exec is not available in Maven Central. In order to install it in your local Maven repository run:

	./gradlew publishToMavenLocal
 
Apache Maven:

    <dependency>
        <groupId>com.github.platan</groupId>
        <artifactId>varnish-exec-(core|junit)</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

Gradle:

    repositories {
        mavenLocal()
    }

    testCompile 'com.github.platan:varnish-exec-(core|junit):0.1.0-SNAPSHOT'

## Usage
JUnit test using rule:

    import com.github.platan.varnishexec.VarnishCommand;
    import com.github.platan.varnishexec.junit.VarnishResource;

    import org.junit.ClassRule;
    ...

    public class MyTest {

        private static final VarnishCommand VARNISH_COMMAND = VarnishCommand.newBuilder()
            .withConfigFile("./src/test/etc/varnish/default.vcl").build();

        @ClassRule
        public static VarnishResource varnishResource = VarnishResource.build(VARNISH_COMMAND);

        @Test
        public void testSomethingWithRunningVarnish() throws Exception {
            int port = varnishResource.getPort();
            ...
        }
    }
In the above example we use a [ClassRule](https://github.com/junit-team/junit/wiki/Rules#classrule). Varnish Cache daemon starts once before all tests run and shutdowns after they are finished.

Another test using API:

    import org.junit.AfterClass;
    import org.junit.BeforeClass;

    import com.github.platan.varnishexec.VarnishCommand;
    import com.github.platan.varnishexec.VarnishExecs;
    import com.github.platan.varnishexec.VarnishProcess;
    ...

    public class MyTest {

        private static VarnishProcess varnishProcess;

        @BeforeClass
        public static void setUpClass() {
            VarnishCommand varnishCommand = VarnishCommand.newBuilder()
					.withConfigFile("./src/test/etc/varnish/default.vcl").build();
            varnishProcess = VarnishExecs.start(varnishCommand);
        }

        @AfterClass
        public static void tearDownClass() {
            varnishProcess.kill();
        }

        @Test
        public void testSomethingWithRunningVarnish() throws Exception {
            int port = varnishProcess.getPort();
            ...
        }
    }

The above examples will start a new process with default values:

    varnishd -a localhost:10080 -F -f ./src/test/etc/varnish/default.vcl -n /tmp

## Customization
Use `VarnishCommand` to override default arguments. Varnish daemon command can be configured by `varnishdCommand` parameter.

All supported `varnishd` options:

option                       | varnishd parameter | parameter         | default value
-----------------------------|--------------------|-------------------|------------------------------------
listening address            | -a                 | address           | localhost:10080
backend server               | -b                 | backend           |
VCL configuration file       | -f                 | configFile        |
management interface address | -T                 | managementAddress |
instance name                | -n                 | name              | /tmp
storage backend              | -s                 | storage           |

Build a command:

    VarnishCommand command = VarnishCommand.newBuilder()
                    .withAddress("localhost", 80)
                    .withConfigFile("service.vcl")
                    .withManagementAddress("localhost", 10000)
                    .withName("service")
                    .withStorage("malloc,1G")
                    .withVarnishdCommand("/usr/sbin/varnishd").build();

Then pass it to `VarnishResource`:

    VarnishResource varnishResource = VarnishResource.build(command);

Or to `VarnishExecs#start`:

    VarnishProcess varnishProcess = VarnishExecs.start(command);

## License
This project is licensed under the MIT license.
