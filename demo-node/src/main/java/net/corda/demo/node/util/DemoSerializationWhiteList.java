package net.corda.demo.node.util;

import com.google.common.collect.ImmutableList;
import net.corda.core.serialization.SerializationWhitelist;
import net.corda.demo.node.exception.DemoFlowException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DemoSerializationWhiteList implements SerializationWhitelist {
    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        return ImmutableList.of(
                DemoFlowException.class
        );
    }
}