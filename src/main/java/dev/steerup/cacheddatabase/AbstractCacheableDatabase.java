package dev.steerup.cacheddatabase;

import dev.steerup.cacheddatabase.util.Keys;
import dev.steerup.cacheddatabase.util.Result;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCacheableDatabase<Element> {

    private final Map<Keys, Result<Element>> cache = Collections.synchronizedMap(new HashMap<>());

    public abstract Result<Element> load(Keys keys);

    public abstract boolean store(Element element, Keys keys);

    public Result<Element> cLoad(Keys keys) {
        if (this.fetchLocalResult(keys).nullable().isEmpty())
            this.cache(this.load(keys).nullable().orElse(null), keys);
        return this.fetchLocalResult(keys);
    }

    public boolean cStore(Element element, Keys keys) {
        this.cache(element, keys);
        return this.store(element, keys);
    }

    public List<Result<Element>> fetchLocalResultList(Keys keys) {
        return this.mapCache(keys).collect(Collectors.toList());
    }

    public Result<Element> fetchLocalResult(Keys keys) {
        return this.mapCache(keys).findFirst().orElse(new Result<>());
    }

    public void cache(Element element, Keys keys) {
        this.cache.put(keys, new Result<>(element));
    }

    public void cache(Element element) {
        this.cache.put(Keys.formatObject(element), new Result<>(element));
    }

    @SafeVarargs
    public final void cache(Element... elements) {
        Arrays.stream(elements).forEach(this::cache);
    }

    public void clear() {
        this.cache.clear();
    }

    public void clear(Keys keys) {
        this.cache.entrySet().removeIf(keysResultEntry -> keysResultEntry.getKey().contains(keys));
    }

    private Stream<Result<Element>> mapCache(Keys keys) {
        return this.cache.entrySet()
                .stream()
                .filter(keysResultEntry -> keysResultEntry.getKey().contains(keys))
                .map(Map.Entry::getValue);
    }
}