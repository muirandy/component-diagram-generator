package com.github.muirandy.living.artifact.api.trace;

import java.util.List;

public interface OpenTracingClient {
    List<String> obtainTraceIds();

    Trace obtainTrace(String jaegerTraceId);
}
