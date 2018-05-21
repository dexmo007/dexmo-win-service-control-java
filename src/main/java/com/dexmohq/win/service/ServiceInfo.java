package com.dexmohq.win.service;

import com.dexmohq.cmd.parse.Index;
import com.dexmohq.cmd.parse.Naming;
import com.dexmohq.cmd.parse.Nested;
import com.dexmohq.cmd.parse.NumberFormat;
import com.google.common.base.CaseFormat;
import lombok.Data;

import static com.dexmohq.cmd.parse.NumberFormatType.HEX;

@Data
@Naming(format = CaseFormat.UPPER_UNDERSCORE)
public class ServiceInfo {
    private String serviceName;
    @Nested
    private ServiceTypeInformation type;
    @Index(1)
    private ServiceState state;
    private Integer win32ExitCode;
    private Integer serviceExitCode;
    @NumberFormat(HEX)
    private Integer checkpoint;
    @NumberFormat(HEX)
    private Integer waitHint;

}
