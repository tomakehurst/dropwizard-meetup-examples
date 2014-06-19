import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.http.Fault.MALFORMED_RESPONSE_CHUNK;
import static com.jayway.jsonassert.JsonAssert.with;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class WireMockTest {

    @Rule
    public MyTestEnvironment testEnvironment = new MyTestEnvironment();
    Client client;

    @Before
    public void init() {
        client = testEnvironment.buildTestClient(); // Build using YAML config
    }

    @Rule
    public WireMockRule mockThingStore = new WireMockRule(wireMockConfig().port(9090));

    @Test
    public void should_return_first_item_from_thing_store() throws Exception {

        mockThingStore.stubFor(get(urlEqualTo("/user-items/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user-items-1.json")));

        ClientResponse response =
                client.resource("http://localhost:" + port() + "/items")
                        .accept(APPLICATION_JSON)
                        .get(ClientResponse.class);

        with(response.getEntityInputStream())
                .assertThat("$.items[0].name", is("Item 1"))
                .assertThat("$.items[0].id", is("1168471"));
    }

    @Test
    public void should_push_an_item_to_the_thing_store_when_added_by_user() throws Exception {

        mockThingStore.stubFor(post(urlEqualTo("/user-items"))
                .willReturn(aResponse()
                        .withStatus(201)));

        client.resource("http://localhost:" + port() + "/items")
                .post(ClientResponse.class, "{ \"id\": \"71283756\", \"name\": \"New Thing\" }");

        mockThingStore.verify(postRequestedFor(urlEqualTo("/user-items"))
                .withRequestBody(containing("71283756")));
    }

    @Test
    public void should_fail_gracefully_when_thing_store_returns_bad_http() throws Exception {

        mockThingStore.stubFor(get(urlEqualTo("/user-items/1"))
                .willReturn(aResponse()
                        .withFault(MALFORMED_RESPONSE_CHUNK)));

        ClientResponse response =
                client.resource("http://localhost:" + port() + "/items")
                        .accept(APPLICATION_JSON)
                        .get(ClientResponse.class);

        assertThat(response.getStatus(), is(500));
        assertThat(response.getEntity(String.class),
                containsString("Sorry - we're having some trouble getting that for you, blah, blah..."));
    }


    @Test
    public void health_check_should_fail_meaningfully_when_thing_store_returns_bad_http() {

        mockThingStore.stubFor(get(urlEqualTo("/user-items/1"))
                .willReturn(aResponse()
                        .withFault(MALFORMED_RESPONSE_CHUNK)));

        ClientResponse response =
                client.resource("http://localhost:" + adminPort() + "/healthcheck")
                        .get(ClientResponse.class);

        assertThat(response.getStatus(), is(500));
        assertThat(response.getEntity(String.class),
                containsString("Thing Store returned an unparseable HTTP response"));
    }

    private int adminPort() {
        return 8081;
    }

    private int port() {
        return testEnvironment.app().getLocalPort();
    }
}

