import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class MockService2Rule implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return null;
    }
}
