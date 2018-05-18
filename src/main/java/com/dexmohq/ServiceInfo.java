package com.dexmohq;

import com.dexmohq.util.Naming;
import com.dexmohq.util.NumberFormat;
import com.dexmohq.util.Skip;
import com.google.common.base.CaseFormat;
import lombok.Data;

import static com.dexmohq.util.NumberFormatType.HEX;
import static com.dexmohq.util.Skip.LAST;

@Data
@Naming(format = CaseFormat.UPPER_UNDERSCORE)
public class ServiceInfo {
    private String serviceName;
    @Skip(n = LAST)
    private ServiceType type;
    @Skip(n = LAST)
    private ServiceState state;
    private Integer win32ExitCode;
    private Integer serviceExitCode;
    @NumberFormat(HEX)
    private Integer checkpoint;
    @NumberFormat(HEX)
    private Integer waitHint;

}
