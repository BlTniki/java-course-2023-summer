package edu.java.service.link.stackoverflow;


public enum ResourceType {
    QUESTIONS("questions");

    public final String raw;

    ResourceType(String raw) {
        this.raw = raw;
    }
}
