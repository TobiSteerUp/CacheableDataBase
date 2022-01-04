package dev.steerup.cacheddatabase.util;

import dev.steerup.cacheddatabase.annotation.Key;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class Keys {

    private final Map<String, Object> parameter = new HashMap<>();
    private final Map<String, String> data = new HashMap<>();

    public static Keys builder() {
        return new Keys();
    }

    public Optional<String> getData(String key) {
        return Optional.ofNullable(this.data.getOrDefault(key, null));
    }

    public Set<Map.Entry<String, Object>> getParameter() {
        return this.parameter.entrySet();
    }

    public <T> T getParameter(Class<T> clazz, String key) {
        return clazz.cast(this.parameter.get(key));
    }

    public Object getParameter(String key) {
        return this.parameter.get(key);
    }

    public Keys data(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    public Keys parameter(String key, Object value) {
        this.parameter.put(key, value);
        return this;
    }

    public boolean contains(Keys keys) {
        Stream<Map.Entry<String, Object>> entryStream = keys.getParameter().stream().filter(entry -> {
            Object object = this.parameter.getOrDefault(entry.getKey(), null);
            if (object == null) return false;
            Object value = entry.getValue();
            if (value == null) return false;
            return object.equals(value);
        });

        long count = entryStream.count();

        if (keys.getParameter().size() != count) {
            return false;
        }

        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            Optional<String> data = keys.getData(entry.getKey());

            if (!(data.isPresent() && data.get().equals(entry.getValue())))
                return false;
        }
        return true;
    }

    public static Keys formatObject(Object object) {
        Keys builder = Keys.builder();
        Class<?> c = object.getClass();

        Arrays.stream(c.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Key.class))
                .forEach(method -> {
                    Key annotation = method.getAnnotation(Key.class);
                    String key = annotation.value();
                    try {
                        Object value = method.invoke(object);
                        builder.parameter(key, value);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
        return builder;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Keys)) {
            return false;
        }

        Keys keys = (Keys) obj;

        return keys.contains(this) && this.contains(keys);
    }
}