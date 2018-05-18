package com.dexmohq;

import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class ServiceManager {

    private final Map<String, ServiceController> services;

    private ServiceManager() {
        services = ServiceUtils.getServices()
                .collect(toMap(identity(), ServiceUtils::getServiceController));
    }

    public static void main(String[] args) {
        new ServiceManager().services.keySet().forEach(System.out::println);
    }


}
