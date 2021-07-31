package me.cyphers.fruitservers.tokenperms.Config;

import me.cyphers.fruitservers.tokenperms.Plugin;

/**
 * Manager for a collection of config files. Recommended to provide methods for getting each of the ConfigFiles it manages
 */
public interface ConfigManager {

    /**
     * Reloads all configs this manager is responsible for
     */
    void reloadConfigs() throws Exception;

    Plugin getPlugin();

}
