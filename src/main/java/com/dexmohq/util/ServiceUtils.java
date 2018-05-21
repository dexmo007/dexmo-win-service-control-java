package com.dexmohq.util;

import com.dexmohq.cmd.parse.LineParser;
import com.dexmohq.cmd.parse.ResultsLineParser;
import com.dexmohq.win.service.CmdServiceController;
import com.dexmohq.win.service.ServiceInfo;
import com.dexmohq.cmd.parse.BeanLineParser;
import com.dexmohq.win.service.ServiceInfos;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public class ServiceUtils {

    private static final BeanLineParser<ServiceInfo> SERVICE_INFO_LINE_PARSER = BeanLineParser.createForType(ServiceInfo.class);

    /**
     * @return a stream of windows services
     */
    public static Stream<String> getServices() {
        return ProcessUtils.exec("net start")
                .getResult()
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
                .getResult()
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
    public static CmdServiceController getServiceController(String serviceName) {
        return new CmdServiceController(getServiceInfo(serviceName));
    }

    private static final LineParser<ServiceInfos> SERVICE_INFOS_LINE_PARSER = ResultsLineParser.createForResultsType(ServiceInfos.class);

    public static List<ServiceInfo> getServiceInfos() {
        return ProcessUtils.exec("sc query", SERVICE_INFOS_LINE_PARSER).getResult().getServiceInfos();
    }

    public static void main(String[] args) {
        for (ServiceInfo serviceInfo : getServiceInfos()) {
            System.out.println(serviceInfo);
        }
    }

}
