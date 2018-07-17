package net.corda.demo.sc.state;

import io.netty.util.internal.StringUtil;
import net.corda.core.serialization.CordaSerializable;

import java.util.Arrays;

@CordaSerializable
public enum Flavour {
    VANILLA("vanilla"),
    CHOCOLATE("chocolate"),
    ORANGE("orange"),
    PINEAPPLE("pineapple"),
    STRAWBERRY("strawberry");

    private final String name;

    Flavour(String name) {
        this.name = name;
    }

    public static Flavour fromText(String name) {
        for (Flavour value : Flavour.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        if (StringUtil.isNullOrEmpty(name)) {
            return VANILLA;
        }
        throw new IllegalArgumentException("unable to find equivalent Flavour, try only the following " + Arrays.toString(Flavour.values()));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
