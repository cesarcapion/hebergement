package fr.epita.assistants.ping.utils;

public interface IConverter<T1, T2> {
    public T2 convert(T1 t1);
}
