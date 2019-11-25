package com.github.muirandy.living.artifact.diagram.domain;

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
}
