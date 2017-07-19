# Gluon CloudLink Enterprise SDK for Spring #

Gluon CloudLink Enterprise SDK for Spring is a client SDK for accessing [Gluon CloudLink](http://com.gluonhq.com/products/cloudlink/)
services from within an enterprise back end infrastructure that is running together with the [Spring Framework](https://spring.io/).

## Documentation ##

* [JavaDoc](http://docs.com.gluonhq.com/cloudlink/enterprise/sdk/spring/javadoc/)
* [Gluon CloudLink](http://docs.com.gluonhq.com/cloudlink)
* [Samples](http://com.gluonhq.com/support/samples/#cloudlink)

## Getting Started ##

### Dependencies ###

#### Gradle ####

    dependencies {
        compile 'com.gluonhq:cloudlink-enterprise-sdk-spring:1.1.0'
    }

#### Maven ####

    <dependencies>
        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>cloudlink-enterprise-sdk-spring</artifactId>
            <version>1.1.0</version>
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

    CloudLinkConfig config = new CloudLinkConfig("YOUR_SERVER_KEY");
    CloudLinkClient client = new SpringCloudLinkClient(config);
