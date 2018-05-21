package com.dexmohq.win.service;

import com.dexmohq.cmd.parse.Index;
import lombok.Data;

/**
 * @author Henrik Drefs
 */
@Data
public class ServiceTypeInformation {

    @Index(1)
    private ServiceType serviceType;
    @Index(2)
    private String info;
}
