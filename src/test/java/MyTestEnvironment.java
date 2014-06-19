import com.sun.jersey.api.client.Client;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class MyTestEnvironment implements TestRule {

    private final DropwizardAppRule<MyConfig> app =
            new DropwizardAppRule<>(MyApp.class, "test.yaml");

    private final InMemoryH2Rule db = new InMemoryH2Rule();
    private final MockService1Rule service1 = new MockService1Rule();
    private final MockService2Rule service2 = new MockService2Rule();

    private final RuleChain rules =
        RuleChain
                .outerRule(db)
                .around(service1)
                .around(service2)
                .around(app);

    @Override
    public Statement apply(Statement base, Description description) {
        return rules.apply(base, description);
    }

    public DropwizardAppRule<MyConfig> app() {
        return app;
    }

    public Client buildTestClient() {
        return app.getConfiguration().buildTestClient(app.getEnvironment());
    }
}


