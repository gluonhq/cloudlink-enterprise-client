# Gluon CloudLink Enterprise SDK for Spring #

Gluon CloudLink Enterprise SDK for Spring is a client SDK for accessing [Gluon CloudLink](http://gluonhq.com/products/cloudlink/)
services from within an enterprise back end infrastructure that is running together with the [Spring Framework](https://spring.io/).

## Documentation ##

* [JavaDoc](http://docs.gluonhq.com/cloudlink/enterprise/sdk/spring/javadoc/)
* [Gluon CloudLink](http://docs.gluonhq.com/cloudlink)
* [Samples](http://gluonhq.com/support/samples/#cloudlink)

## Getting Started ##

### Dependencies ###

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-sdk-spring:1.2.0'
    }

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-sdk-spring</artifactId>
            <version>1.2.0</version>
        </dependency>
    </dependencies>

### Instantiation ###

#### Injection ####

A property containing the server key of your Gluon CloudLink Application:

    gluon.cloudlink.serverKey=YOUR_SERVER_KEY

Then inject the CloudLinkClient instance as follows:

    private final CloudLinkClient client;

    @Autowired
    public MyService(CloudLinkClient client) {
        this.client = client;
    }

#### Manual ####

    CloudLinkClientConfig config = new CloudLinkClientConfig("YOUR_SERVER_KEY");
    CloudLinkClient client = new CloudLinkClient(config);
