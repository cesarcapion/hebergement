package fr.epita.assistants.ping.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum Feature {
    GIT("git");

    public final String label;

    private static final Map<String, Feature> BY_LABEL = new HashMap<>();

    static {
        for (Feature e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    // ... fields, constructor, methods

    public static Feature valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
    private Feature(String label) {
        this.label = label;
    }

    public boolean exists(String feature) {
        return BY_LABEL.containsKey(feature);
    }
}
