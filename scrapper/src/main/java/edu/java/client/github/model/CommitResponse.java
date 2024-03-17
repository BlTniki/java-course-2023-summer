package edu.java.client.github.model;

public record CommitResponse(
    String sha,
    Commit commit
) {}
