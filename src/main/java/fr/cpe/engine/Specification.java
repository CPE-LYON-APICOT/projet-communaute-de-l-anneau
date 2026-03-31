package fr.cpe.engine;

public interface Specification<T> {
    boolean isSatisfiedBy(T item);
}