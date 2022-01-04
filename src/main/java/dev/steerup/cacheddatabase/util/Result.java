package dev.steerup.cacheddatabase.util;

import java.util.Optional;

public class Result<Element> {

    private final Element element;
    private final long storedAt;

    public Result(Element element) {
        this.element = element;
        this.storedAt = System.currentTimeMillis();
    }

    public Result() {
        this.element = null;
        this.storedAt = System.currentTimeMillis();
    }

    public Optional<Element> nullable() {
        return Optional.ofNullable(element);
    }

    public Element get() {
        return this.element;
    }

    public long storedAt() {
        return this.storedAt;
    }
}