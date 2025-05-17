package project_biu.configs;

/**
 * MathExampleConfig is a simple predefined configuration for testing and demonstration.
 * It sets up a small pipeline of mathematical operations using three BinOpAgents:
 *  - Adds inputs A and B to produce R1
 *  - Subtracts B from A to produce R2
 *  - Multiplies R1 and R2 to produce R3
 *
 * This class is useful for verifying the functioning of BinOpAgents and the topic system.
 */
public class MathExampleConfig implements Config {

    /**
     * Initializes the configuration by creating and linking BinOpAgent instances.
     * Each agent is automatically subscribed to its input topics and publishes to an output topic.
     *
     * Operation flow:
     *   R1 = A + B
     *   R2 = A - B
     *   R3 = R1 * R2
     */
    @Override
    public void create() {
        // Create agent to compute sum of A and B -> publish to R1
        new BinOpAgent("plus", "A", "B", "R1", (x, y) -> x + y);

        // Create agent to compute difference A - B -> publish to R2
        new BinOpAgent("minus", "A", "B", "R2", (x, y) -> x - y);

        // Create agent to multiply results R1 and R2 -> publish to R3
        new BinOpAgent("mul", "R1", "R2", "R3", (x, y) -> x * y);
    }

    /**
     * Returns a descriptive name for this configuration.
     *
     * @return the name of the configuration
     */
    @Override
    public String getName() {
        return "Math Example";
    }

    /**
     * Specifies the version number of the configuration implementation.
     * Useful for compatibility tracking or version control.
     *
     * @return version identifier
     */
    @Override
    public int getVersion() {
        return 1;
    }

    /**
     * Clean-up method for configuration shutdown.
     * Currently unused but included for interface compliance.
     */
    @Override
    public void close() {}
}
