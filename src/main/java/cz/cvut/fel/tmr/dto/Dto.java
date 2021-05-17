package cz.cvut.fel.tmr.dto;

public interface Dto<T> {
    public T buildFromDto();
    public T update(T t);
}
