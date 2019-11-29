package com.github.muirandy.living.artifact.api.chain;

public interface OpenTracingClient {
    Trace obtainTrace(String jaegerTraceId);
}
