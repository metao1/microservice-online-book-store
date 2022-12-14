package com.metao.book.order.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    NEW("new"),
    ACCEPT("accept"),
    CONFIRM("confirm"),
    REJECT("reject"),
    PAYMENT("payment"),
    PRODUCT("product"),
    ROLLBACK("rollback");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonCreator
    public Status toStatus(String value) {
        for (Status status : Status.values()) {
            if (value.equals(this.value)) {
                return status;
            }
        }
        throw new RuntimeException("No value matched for Status");
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

}
