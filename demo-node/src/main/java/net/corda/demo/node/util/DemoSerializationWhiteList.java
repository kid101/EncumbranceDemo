package net.corda.demo.node.util;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.serialization.SerializationWhitelist;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;

public class DemoSerializationWhiteList implements SerializationWhitelist {
    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        return ImmutableList.of(HashSet.class, Date.class, java.util.Date.class,
                Instant.class, TimeWindow.class, Double.class
        );
    }
}