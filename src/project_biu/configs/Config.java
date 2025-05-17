package project_biu.configs;

/*
 * Defines a generic contract for system configuration components.
 * Any class implementing this interface should provide mechanisms for:
 *  - Initialization/setup (`create`)
 *  - Name/version identification
 *  - Graceful shutdown and cleanup (`close`)
 */
public interface Config {

    /**
     * Performs all required setup steps for activating the configuration.
     *
     * @throws Exception if something goes wrong during setup
     */
    void create() throws Exception;

    /**
     * @return Identifier string representing this config instance
     */
    String getName();

    /**
     * @return Integer version tag associated with this config
     */
    int getVersion();

    /**
     * Signals that this configuration is no longer needed,
     * and cleans up resources if necessary.
     */
    void close();
}
