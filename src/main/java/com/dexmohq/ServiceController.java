package com.dexmohq;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServiceController {

    private ServiceInfo serviceInfo;

    public String getServiceName() {
        return serviceInfo.getServiceName();
    }

    public void start() {
        //todo impl
    }

    public void stop() {
        //todo impl
    }

    public void refresh() {
        serviceInfo = ServiceUtils.getServiceInfo(getServiceName());
    }

}
