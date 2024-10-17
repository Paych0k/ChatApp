package edu.school21.repositories;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void save(T t);

    List<T> findAll();

    Optional<T> findById(long id);

    void deleteById(int id);
}
