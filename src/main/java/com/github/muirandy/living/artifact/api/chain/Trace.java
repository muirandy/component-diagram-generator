package com.github.muirandy.living.artifact.api.chain;

import java.util.ArrayList;
import java.util.List;

public class Trace {
    public final List<Span> spans = new ArrayList<>();

    public void addSpan(Span span) {
        spans.add(span);
    }

    public boolean isEmpty() {
        return spans.isEmpty();
    }
}
