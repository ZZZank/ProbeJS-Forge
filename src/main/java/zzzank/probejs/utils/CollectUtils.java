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

    static <T> List<T> ofList(T... elements) {
        val list = new ArrayList<T>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
    }

    static <I, O> List<O> mapToList(Collection<I> collection, Function<I, O> mapper) {
        val l = new ArrayList<O>(collection.size());
        for (I i : collection) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    static int calcMapExpectedSize(int elementCount) {
        return calcMapExpectedSize(elementCount, 0.75F);
    }

    static int calcMapExpectedSize(int elementCount, float loadFactor) {
        return (int) Math.floor((elementCount / loadFactor) + 1);
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

    @Nullable
    static <T> T anyIn(Iterable<T> iterable) {
        val iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Nullable
    static <T> T anyIn(Stream<T> stream) {
        return stream.findAny().orElse(null);
    }
}
