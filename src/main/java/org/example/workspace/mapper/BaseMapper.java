package org.example.workspace.mapper;

public interface BaseMapper<E, D> {
    D toDto(E entity);
}
