import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.jayway.jsonassert.JsonAssert.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AcceptanceTest {

    @Rule
    public MyTestEnvironment testEnvironment = new MyTestEnvironment();
    Client client;

    @Before
    public void init() {
        client = testEnvironment.buildTestClient(); // Build using YAML config
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_return_3_list_items_initially() throws Exception {
        ClientResponse response =
                client.resource("http://localhost:" + port() + "/items")
                .accept(APPLICATION_JSON)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(200));
        with(response.getEntityInputStream())
                .assertThat("$.items", is(collectionWithSize(equalTo(2))));
    }

    private int port() {
        return testEnvironment.app().getLocalPort();
    }
}


