package com.github.muirandy.living.artifact.api.trace;

public interface OpenTracingClient {
    Trace obtainTrace(String jaegerTraceId);
}
