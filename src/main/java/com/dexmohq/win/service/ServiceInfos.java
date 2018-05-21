package com.dexmohq.win.service;

import com.dexmohq.cmd.parse.Delimiter;
import com.dexmohq.cmd.parse.DelimiterType;
import com.dexmohq.cmd.parse.Results;
import lombok.Data;

import java.util.List;

/**
 * @author Henrik Drefs
 */
@Data
@Results
public class ServiceInfos {

    @Delimiter(DelimiterType.EMPTY_LINE)
    private List<ServiceInfo> serviceInfos;

}
