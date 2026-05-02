package fr.cpe.engine;

public interface Specification<T> {
    boolean isSatisfiedBy(T item);

    /** Description humaine de la specification, utilisee dans les messages
     *  de victoire pour expliquer pourquoi la partie est terminee. */
    default String getDescription() {
        return getClass().getSimpleName();
    }
}