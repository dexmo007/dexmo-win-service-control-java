package com.dexmohq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceType {
    WIN32_OWN_PROCESS(10);

    private final int code;

}
