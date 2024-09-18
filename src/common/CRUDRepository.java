package common;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T> {
    int add(T t);

    int update(T t);

    int delete(int id);

    List<T> getAll();

    Optional<T> findBy(int id);
}