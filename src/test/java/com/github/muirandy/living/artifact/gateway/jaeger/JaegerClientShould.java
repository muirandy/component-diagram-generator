package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Trace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JaegerClientShould {
    private static final String JAEGER_SERVER = "jaegerServer";
    private static final int JAEGER_PORT = 16686;
    private static final String JAEGER_TRACE_ID = "0123456789abcdef";

    @Spy
    private JaegerClient jaegerClient = new JaegerClient(JAEGER_SERVER, JAEGER_PORT);

    @Test
    void name() {
        Trace trace = jaegerClient.obtainTrace(JAEGER_TRACE_ID);

    }
}