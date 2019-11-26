package com.github.muirandy.living.artifact.api.chain;

import com.github.muirandy.living.artifact.diagram.domain.Chain;

public interface ChainBuilder {
    Chain build(String jaegerTraceId);
}
