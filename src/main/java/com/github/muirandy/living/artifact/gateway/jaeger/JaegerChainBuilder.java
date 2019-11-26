package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.diagram.domain.Chain;

public class JaegerChainBuilder implements ChainBuilder {
    public JaegerChainBuilder(int jaegerPort) {

    }

    @Override
    public Chain build(String jaegerTraceId) {
        return null;
    }
}
