package com.parentcircle.utils;

/**
 * Loads test users from configuration. Credentials are pulled from
 * config.properties (or overridden via -D system properties / env). Never
 * hard-code credentials in test classes.
 */
public final class TestUsers {

    private TestUsers() {}

    public static TestUser standard() {
        String email = ConfigReader.get("test.email");
        String password = ConfigReader.get("test.password");
        if (email == null || password == null) {
            throw new IllegalStateException(
                    "Missing test.email or test.password — set them in config.properties "
                            + "or pass -Dtest.email=... -Dtest.password=...");
        }
        return new TestUser(email, password);
    }
}
