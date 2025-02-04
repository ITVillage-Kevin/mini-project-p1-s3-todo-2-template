package com.itvillage.common.converter;

public interface BiDirectionalConverter<S, T> {
    T convert(S s);
    S revert(T t);
}
