package project_biu.configs;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import project_biu.graph.Agent;

/*
 * GenericConfig:
 * A file-driven implementation of the Config interface.
 * This class dynamically creates and manages agents using a plain-text configuration file.
 *
 * Key Responsibilities:
 * - Parse and validate configuration blocks (3 lines each)
 * - Instantiate agents by class name using reflection
 * - Maintain a list of agents and provide cleanup through close()
 */
public class GenericConfig implements Config {

    String configFileName;
    private List<Agent> agents = new ArrayList<>();

    /**
     * Assigns the configuration file name to be used when loading agents.
     *
     * @param confFile Path to the configuration file
     */
    public void setConfFile(String confFile) {
        this.configFileName = confFile;
    }

    /**
     * Loads configuration from a file and instantiates agents accordingly.
     * Each agent is defined using a 3-line block: class name, subscriptions, publications.
     *
     * @throws Exception if file I/O, parsing, or instantiation fails
     */
    @Override
    public void create() throws Exception {
        List<String> lines;
        Path filePath = Paths.get(this.configFileName);

        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new Exception("Error reading configuration file: " + this.configFileName);
        }

        if (!ifConfigFileValid(lines)) {
            throw new Exception("Invalid configuration format. File must be in 3-line blocks.");
        }

        // Iterate over config blocks
        for (int i = 0; i < lines.size(); i += 3) {
            try {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                // Load class dynamically
                Class<?> agentClass = Class.forName(className);
                Constructor<?> constructor = agentClass.getConstructor(String[].class, String[].class);
                Object agent = constructor.newInstance((Object) subs, (Object) pubs);

                // Wrap in a ParallelAgent and track it
                ParallelAgent p_agent = new ParallelAgent((Agent) agent, 1);
                this.agents.add(p_agent);

            } catch (ClassNotFoundException e) {
                throw new Exception("Class not found: " + lines.get(i));
            } catch (NoSuchMethodException e) {
                throw new Exception("Missing expected constructor for: " + lines.get(i));
            } catch (Exception e) {
                throw new Exception("Failed to create agent for: " + lines.get(i));
            }
        }
    }

    /**
     * @return Configuration name identifier
     */
    @Override
    public String getName() {
        return "GenericConfig";
    }

    /**
     * @return Version number (currently always 0)
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     * Closes all agents initialized by this configuration.
     */
    @Override
    public void close() {
        for (Agent a : agents) {
            a.close();
        }
    }

    /**
     * Validates that the config file follows the expected format:
     *  - Number of lines is divisible by 3
     *  - Each 3-line block is non-empty and references a valid class
     *
     * @param lines Raw lines from the config file
     * @return true if format is valid
     */
    private boolean ifConfigFileValid(List<String> lines) {
        int numLines = lines.size();
        if (numLines == 0 || numLines % 3 != 0) {
            return false;
        }

        for (int i = 0; i < lines.size(); i += 3) {
            if (!isValidAgentBlock(lines, i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates a single agent block from the config file.
     * Ensures all lines are non-empty and the specified class exists.
     *
     * @param lines Full list of lines
     * @param index Starting line index of the block
     * @return true if block is well-formed
     */
    private boolean isValidAgentBlock(List<String> lines, int index) {
        if (lines.get(index).trim().isEmpty() ||
                lines.get(index + 1).trim().isEmpty() ||
                lines.get(index + 2).trim().isEmpty()) {
            return false;
        }

        String className = lines.get(index).trim();
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class in config: " + className);
            return false;
        }

        return true;
    }

    /**
     * @return Number of agents currently configured
     */
    public int getNumberOfAgents() {
        return agents.size();
    }

    /**
     * @return List of all agents created by this configuration
     */
    public List<Agent> getAgents() {
        return this.agents;
    }
}
