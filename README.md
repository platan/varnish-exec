# Varnish Exec [![Build Status](https://travis-ci.org/platan/varnish-exec.svg?branch=master)](https://travis-ci.org/platan/varnish-exec) [![Coverage Status](https://coveralls.io/repos/platan/varnish-exec/badge.svg?branch=master)](https://coveralls.io/r/platan/varnish-exec?branch=master)
Java library and [JUnit](http://junit.org/) [rule](https://github.com/junit-team/junit/wiki/Rules) for running [Varnish Cache](https://www.varnish-cache.org/) daemon.

- [Modules](#modules)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Customization](#customization)
- [Changelog](#changelog)
- [License](#license)

## Modules
- varnish-exec-core - Core module
- varnish-exec-junit - Integration with the JUnit

## Requirements
- JDK 8
- Varnish Cache 3.0 or 4.0

## Installation

Varnish Exec is available in Maven Central.

Apache Maven:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.platan</groupId>
        <artifactId>varnish-exec-(core|junit)</artifactId>
        <version>0.1.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
Gradle:
```gradle
repositories {
    mavenCentral()
}

dependencies {
    testCompile 'com.github.platan:varnish-exec-(core|junit):0.1.0'
}
```

In order to install it in your local Maven repository run:
```shell
./gradlew install
```

## Usage
JUnit test using rule:
```java
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
```
In the above example we use a [ClassRule](https://github.com/junit-team/junit/wiki/Rules#classrule). Varnish Cache daemon starts once before all tests run and shutdowns after they are finished.

Another test using API:
```java
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
```
The above examples will start a new process with default values:
```shell
varnishd -a localhost:10080 -F -f ./src/test/etc/varnish/default.vcl -n /tmp
```
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
```java
VarnishCommand command = VarnishCommand.newBuilder()
                .withAddress("localhost", 80)
                .withConfigFile("service.vcl")
                .withManagementAddress("localhost", 10000)
                .withName("service")
                .withStorage("malloc,1G")
                .withVarnishdCommand("/usr/sbin/varnishd").build();
```
Then pass it to `VarnishResource`:
```java
VarnishResource varnishResource = VarnishResource.build(command);
```
Or to `VarnishExecs#start`:
```java
VarnishProcess varnishProcess = VarnishExecs.start(command);
```

## Changelog

### 0.1.0 (2015-01-25)
- Initial release

## License
This project is licensed under the MIT license.
