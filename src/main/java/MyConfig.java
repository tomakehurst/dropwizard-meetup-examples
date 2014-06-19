import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jersey.api.client.Client;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;

public class MyConfig extends Configuration {

    @JsonProperty
    private JerseyClientConfiguration testClient = new JerseyClientConfiguration();

    public Client buildTestClient(Environment environment) {
        return new JerseyClientBuilder(environment).using(testClient).build("test-client");

    }
}
