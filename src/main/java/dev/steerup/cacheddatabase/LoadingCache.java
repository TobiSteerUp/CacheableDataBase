package dev.steerup.cacheddatabase;

import dev.steerup.cacheddatabase.util.Keys;
import dev.steerup.cacheddatabase.util.Result;

public abstract class LoadingCache<Key, Value> {

    private final AbstractCacheableDatabase<Value> database = new AbstractCacheableDatabase<Value>() {
        @Override
        public Result<Value> load(Keys keys) {
            Key key = keys.getParameter("key");
            Value value = LoadingCache.this.load(key);
            return new Result<>(value);
        }

        @Override
        public boolean store(Value value, Keys keys) {
            return false;
        }
    };

    public Result<Value> get(Key key) {
        return this.database.cLoad(this.getKey(key));
    }

    public Result<Value> getCache(Key key) {
        return this.database.fetchLocalResult(this.getKey(key));
    }

    public Value getUnchecked(Key key) {
        return this.get(key).get();
    }

    public void put(Key key, Value value) {
        this.database.cache(value, this.getKey(key));
    }

    private Keys getKey(Key key) {
        return Keys.builder().parameter("key", key);
    }

    public abstract Value load(Key key);
}