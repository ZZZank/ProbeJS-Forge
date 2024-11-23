package zzzank.probejs.utils;

import lombok.val;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * utils for collections
 *
 * @author ZZZank
 */
public interface CollectUtils {

    @SafeVarargs
    static <T> List<T> ofList(T... elements) {
        val list = new ArrayList<T>(elements.length);
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

    static <I, O> List<O> mapToList(Collection<I> collection, Function<I, O> mapper) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        val l = new ArrayList<O>(collection.size());
        for (I i : collection) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    static <I, O> List<O> mapToList(I[] collection, Function<I, O> mapper) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        val l = new ArrayList<O>(collection.length);
        for (I i : collection) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    static int calcMapExpectedSize(int elementCount) {
        return calcMapExpectedSize(elementCount, 0.75F);
    }

    static int calcMapExpectedSize(int elementCount, float loadFactor) {
        return (int) Math.ceil(elementCount / loadFactor);
    }

    static <K, V> HashMap<K, V> ofSizedMap(int expectedSize) {
        return new HashMap<>(calcMapExpectedSize(expectedSize));
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1) {
        val m = new HashMap<K, V>(calcMapExpectedSize(1));
        m.put(k1, v1);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1, K k2, V v2) {
        val m = new HashMap<K, V>(calcMapExpectedSize(2));
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        val m = new HashMap<K, V>(calcMapExpectedSize(3));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        val m = new HashMap<K, V>(calcMapExpectedSize(4));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        val m = new HashMap<K, V>(calcMapExpectedSize(5));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        val m = new HashMap<K, V>(calcMapExpectedSize(6));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        m.put(k6, v6);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(
        K k1, V v1,
        K k2, V v2,
        K k3, V v3,
        K k4, V v4,
        K k5, V v5,
        K k6, V v6,
        K k7, V v7
    ) {
        val m = new HashMap<K, V>(calcMapExpectedSize(7));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        m.put(k6, v6);
        m.put(k7, v7);
        return m;
    }

    static <K, V> HashMap<K, V> ofMap(
        K k1, V v1,
        K k2, V v2,
        K k3, V v3,
        K k4, V v4,
        K k5, V v5,
        K k6, V v6,
        K k7, V v7,
        K k8, V v8
    ) {
        val m = new HashMap<K, V>(calcMapExpectedSize(8));
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        m.put(k6, v6);
        m.put(k7, v7);
        m.put(k8, v8);
        return m;
    }

    @Nullable
    static <T> T anyIn(Iterable<T> iterable) {
        val iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Nullable
    static <T> T anyIn(Stream<T> stream) {
        return stream.findAny().orElse(null);
    }

    static <T> Iterator<T> enumToItr(Enumeration<T> enumeration) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }

    static <T> Iterable<T> enumToIterable(Enumeration<T> enumeration) {
        return () -> enumToItr(enumeration);
    }
}
