package com.github.muirandy.living.artifact.api.diagram;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class Chain {
    private List<Link> links = new ArrayList<>();

    public void add(Link link) {
        links.add(link);
    }

    public boolean isEmpty() {
        return links.isEmpty();
    }

    public List<Link> getLinks() {
        return links;
    }

    public int getSize() {
        return links.size();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
