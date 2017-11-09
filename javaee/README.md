# Gluon CloudLink Enterprise SDK for Java EE #

Gluon CloudLink Enterprise SDK for Java EE is a client SDK for accessing [Gluon CloudLink](http://gluonhq.com/products/cloudlink/)
services from within an enterprise back end infrastructure that is running inside a [Java EE environment](http://docs.oracle.com/javaee/).

## Documentation ##

* [JavaDoc](http://docs.gluonhq.com/cloudlink/enterprise/sdk/javaee/javadoc/)
* [Gluon CloudLink](http://docs.gluonhq.com/cloudlink)
* [Samples](http://gluonhq.com/support/samples/#cloudlink)

## Getting Started ##

### Dependencies ###

When your application is running inside a Java EE 8 environment, only a single maven dependency is required to
use the CloudLink Enterprise SDK for Java EE. When you are running inside a Java EE 7 environment, you need to explicitly
provide an implementation of the Java API for JSON Binding as well. You can use [Yasson](https://github.com/eclipse/yasson),
which is the reference implementation for JSON-B.

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-sdk-javaee:1.2.2'

        // only required if running with Java EE 7
        runtime 'org.eclipse:yasson:1.0'
    }

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-sdk-javaee</artifactId>
            <version>1.2.2</version>
        </dependency>

        <!-- only required if running with Java EE 7 -->
        <dependency>
            <groupId>org.eclipse</groupId>
            <artifactId>yasson</artifactId>
            <version>1.0</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

### Instantiation ###

#### With CDI ####

    @Inject
    @CloudLinkConfig(serverKey = "YOUR_SERVER_KEY")
    private CloudLinkClient client;

#### Manual ####

    CloudLinkClientConfig config = new CloudLinkClientConfig("YOUR_SERVER_KEY");
    CloudLinkClient client = new CloudLinkClient(config);
