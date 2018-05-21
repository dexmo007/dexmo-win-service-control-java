package com.dexmohq.win.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceType {
    WIN32_OWN_PROCESS("10"),WIN32_SHARE_PROCESS("20"),WIN32("30"),ERROR("f0"),USER_SHARE_PROCESS("e0");

    private final String code;

}
