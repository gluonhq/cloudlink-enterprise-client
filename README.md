# Gluon CloudLink Enterprise Client #

Gluon CloudLink Enterprise Client is a Java client for accessing [Gluon CloudLink](http://com.gluonhq.com/products/cloudlink/)
services from within an enterprise back end infrastructure. There are currently two implementations:

* Java EE for running inside a [Java EE environment](http://docs.oracle.com/javaee/)
* Spring for running together with the [Spring Framework](https://spring.io/)

## Documentation ##

* [JavaDoc](http://docs.com.gluonhq.com/cloudlink/client/javadoc/)
* [Gluon CloudLink](http://docs.com.gluonhq.com/cloudlink)
* [Samples](http://com.gluonhq.com/support/samples/#cloudlink)

## Running with Java EE ##

### Dependencies ###

When your application is running inside a Java EE 8 environment, only a single maven dependency is required to
use the CloudLink Enterprise Client. When you are running inside a Java EE 7 environment, you need to explicitly
provide an implementation of the Java API for JSON Binding as well. You can use [Yasson](https://github.com/eclipse/yasson),
which is the reference implementation for JSON-B.

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-client-javaee</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>

    <!-- only required if running with Java EE 7 -->
    <repositories>
	    <repository>
            <id>Eclipse Yasson Releases</id>
            <url>https://repo.eclipse.org/content/repositories/releases/</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>org.eclipse</groupId>
            <artifactId>yasson</artifactId>
            <version>1.0.0-M2</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-client-javaee:1.0.0'
    }

    // only required if running with Java EE 7
    repositories {
        maven {
            url 'https://repo.eclipse.org/content/repositories/releases/'
        }
    }
    dependencies {
        runtime 'org.eclipse:yasson:1.0.0-M2'
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
            <artifactId>cloudlink-enterprise-client-spring</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-client-spring:1.0.0'
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
