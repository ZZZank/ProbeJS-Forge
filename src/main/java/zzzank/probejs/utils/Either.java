package zzzank.probejs.utils;

/**
 * @author ZZZank
 */
public interface Either<L, R> {

    static <L, R> Either<L, R> ofLeft(L value) {
        return new Left<>(value);
    }

    static <L, R> Either<L, R> ofRight(R value) {
        return new Right<>(value);
    }

    static <L, R> Either<L, R> ofNone(boolean isRight) {
        return (Either<L, R>) (isRight ? None.RIGHT : None.LEFT);
    }

    L left();

    R right();

    Object value();

    boolean isLeft();

    boolean isRight();

    L leftOrElse(L fallback);

    R rightOrElse(R fallback);

    record Left<L, R>(L value) implements Either<L, R> {

        @Override
        public L left() {
            return value;
        }

        @Override
        public R right() {
            throw new IllegalStateException();
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L leftOrElse(L fallback) {
            return value;
        }

        @Override
        public R rightOrElse(R fallback) {
            return fallback;
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {

        @Override
        public L left() {
            throw new IllegalStateException();
        }

        @Override
        public R right() {
            return value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L leftOrElse(L fallback) {
            return fallback;
        }

        @Override
        public R rightOrElse(R fallback) {
            return value;
        }
    }

    record None<L, R>(boolean isRight) implements Either<L, R> {

        private static final None<Object, Object> LEFT = new None<>(false);
        private static final None<Object, Object> RIGHT = new None<>(false);

        @Override
        public L left() {
            if (isRight) {
                throw new IllegalStateException();
            }
            return null;
        }

        @Override
        public R right() {
            if (isRight) {
                return null;
            }
            throw new IllegalStateException();
        }

        @Override
        public Object value() {
            return null;
        }

        @Override
        public boolean isLeft() {
            return !isRight;
        }

        @Override
        public L leftOrElse(L fallback) {
            return isRight ? fallback : null;
        }

        @Override
        public R rightOrElse(R fallback) {
            return isRight ? null : fallback;
        }
    }
}
