package com.probejs.util;

import java.util.Objects;

/**
 * @author ZZZank
 */
public final class Either<L, R> {
    private final Object value;
    private final boolean isRight;

    public Either<?, R> ofRight(R right) {
        return new Either<>(right, true);
    }

    public Either<L, ?> ofLeft(L left) {
        return new Either<>(left, false);
    }

    private Either(Object value, boolean isRight) {
        this.value = value;
        this.isRight = isRight;
    }

    public L left() {
        if (isRight) {
            throw new IllegalStateException();
        }
        return (L) value;
    }

    public R right() {
        if (!isRight) {
            throw new IllegalStateException();
        }
        return (R) value;
    }

    public L leftOrElse(L fallback) {
        if (isRight) {
            return fallback;
        }
        return (L) value;
    }

    public Object raw() {
        return value;
    }

    public boolean isRight() {
        return isRight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Either) obj;
        return Objects.equals(this.value, that.value) &&
            this.isRight == that.isRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isRight);
    }

    @Override
    public String toString() {
        return "Either[" +
            "value=" + value + ", " +
            "isRight=" + isRight + ']';
    }
}
