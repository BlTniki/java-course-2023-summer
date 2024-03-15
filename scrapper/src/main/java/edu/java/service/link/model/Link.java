package edu.java.service.link.model;

import java.net.URI;

/**
 * Данная модель является комбинацией link и subscription из БД.
 */
public record Link(
    long id,
    URI link,
    String alias
) {}
