package com.dexmohq;

import com.dexmohq.util.LineParser;
import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

@UtilityClass
public class ServiceUtils {

    private static final LineParser<ServiceInfo> SERVICE_INFO_LINE_PARSER = LineParser.createForType(ServiceInfo.class);

    /**
     * @return a stream of windows services
     */
    public static Stream<String> getServices() {
        return ProcessUtils.exec("net start")
                .skip(2)
                .map(String::trim)
                .takeWhile(s -> !s.isEmpty());
    }

    /**
     * Get the current info for a given service
     *
     * @param serviceName the name of the service to find
     * @return service info
     */
    public static ServiceInfo getServiceInfo(String serviceName) {
        final Stream<String> lines = ProcessUtils.exec("sc query \"" + serviceName + "\"")
                .map(String::trim)
                .filter(s -> !s.isEmpty());
        return SERVICE_INFO_LINE_PARSER.parse(lines);
    }

    /**
     * Gets a service controller for a given service name
     *
     * @param serviceName the name of the service to find
     * @return service controller
     */
    public static ServiceController getServiceController(String serviceName) {
        return new ServiceController(getServiceInfo(serviceName));
    }

}
