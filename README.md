# Gluon CloudLink Enterprise SDK #

Gluon CloudLink Enterprise SDK is a client SDK for accessing [Gluon CloudLink](http://com.gluonhq.com/products/cloudlink/)
services from within an enterprise back end infrastructure. There are currently two implementations:

* [Java EE](/javaee) for running inside a [Java EE environment](http://docs.oracle.com/javaee/)
* [Spring](/spring) for running together with the [Spring Framework](https://spring.io/)

## Documentation ##

* [JavaDoc](http://docs.com.gluonhq.com/cloudlink/client/javadoc/)
* [Gluon CloudLink](http://docs.com.gluonhq.com/cloudlink)
* [Samples](http://com.gluonhq.com/support/samples/#cloudlink)

## Running with Java EE ##

### Dependencies ###

When your application is running inside a Java EE 8 environment, only a single maven dependency is required to
use the CloudLink Enterprise Java SDK. When you are running inside a Java EE 7 environment, you need to explicitly
provide an implementation of the Java API for JSON Binding as well. You can use [Yasson](https://github.com/eclipse/yasson),
which is the reference implementation for JSON-B.

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-sdk-javaee</artifactId>
            <version>1.1.0</version>
        </dependency>

        <!-- only required if running with Java EE 7 -->
        <dependency>
            <groupId>org.eclipse</groupId>
            <artifactId>yasson</artifactId>
            <version>1.0</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-sdk-javaee:1.1.0'

        // only required if running with Java EE 7
        runtime 'org.eclipse:yasson:1.0'
    }

### Instantiation ###

#### Manually ####

    CloudLinkConfig config = new CloudLinkConfig("YOUR_SERVER_KEY");
    CloudLinkClient client = new JavaEECloudLinkClient(config);

#### Injection ####

    @Inject
    @CloudLinkConfig(serverKey = "YOUR_SERVER_KEY")
    private CloudLinkClient client;

## Running with Spring ##

### Dependencies ###

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-sdk-spring</artifactId>
            <version>1.1.0</version>
        </dependency>
    </dependencies>

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-sdk-spring:1.1.0'
    }

### Instantiation ###

#### Manually ####

    CloudLinkConfig config = new CloudLinkConfig("YOUR_SERVER_KEY");
    CloudLinkClient client = new SpringCloudLinkClient(config);

#### Injection ####

A property containing the server key of your Gluon CloudLink Application:

    gluon.cloudlink.serverKey=YOUR_SERVER_KEY

Then inject the CloudLinkClient instance as follows:

    private final CloudLinkClient client;

    @Autowired
    public MyService(CloudLinkClient client) {
        this.client = client;
    }
