package common;

import java.util.List;

public interface GlobalService<T> {
    List<T> getAll();

    T getBy(int id);

    int deleteBy(int id);
}