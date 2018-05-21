package com.dexmohq.win.service;

import com.dexmohq.util.ProcessUtils;
import com.dexmohq.util.ServiceUtils;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@AllArgsConstructor
public class CmdServiceController implements ServiceController {

    private ServiceInfo serviceInfo;

    @Override
    public String getServiceName() {
        return serviceInfo.getServiceName();
    }

    @Override
    public void start() {
        ProcessUtils.exec("net start " + getServiceNameInQuotes());
    }

    @Override
    public void stop() {
        ProcessUtils.exec("net stop " + getServiceNameInQuotes());
    }

    @Override
    public void refresh() {
        serviceInfo = ServiceUtils.getServiceInfo(getServiceName());
    }

    private String getServiceNameInQuotes() {
        return "\"" + getServiceName() + "\"";
    }

}
