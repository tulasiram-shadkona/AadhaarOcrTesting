package com.example.robolectrictestframework.utils;

import java.util.UUID;

public class Tools {

    public static String generateUuid() throws Exception {
        try {
            return UUID.randomUUID().toString();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
