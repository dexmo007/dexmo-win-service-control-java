package com.dexmohq.win.service;

/**
 * @author Henrik Drefs
 */
public interface ServiceController {
    /**
     * The name of the service to be controlled
     *
     * @return a string that serves as unique identifier of the controlled service
     */
    String getServiceName();

    /**
     * Start the controlled service
     */
    void start();

    /**
     * Stop the controlled service
     */
    void stop();

    /**
     * Refresh the information about the controlled service
     */
    void refresh();
}
