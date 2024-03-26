package edu.java.domain.link.service.stackoverflow;


public enum ResourceType {
    QUESTIONS("questions");

    public final String raw;

    ResourceType(String raw) {
        this.raw = raw;
    }
}
