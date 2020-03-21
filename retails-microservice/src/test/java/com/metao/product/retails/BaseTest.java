package com.metao.product.retails;

import java.time.Instant;
import java.util.UUID;

public class BaseTest {

    protected static final String USER_ID = UUID.randomUUID().toString();

    protected Instant NOW() {
        return Instant.now();
    }
}
