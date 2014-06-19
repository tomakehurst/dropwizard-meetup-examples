import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class InMemoryH2Rule implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return null;
    }
}
