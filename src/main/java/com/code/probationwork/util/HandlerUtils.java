package com.code.probationwork.util;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandlerUtils {
    public static void logException(Exception e) {
        log.error("found an Exception：", e);
    }
}
