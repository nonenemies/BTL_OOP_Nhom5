package dao;

import java.util.ArrayList;

public interface BaseDao<T>{
    ArrayList<T> selectAll();

    T selectByID (int id);

    void insert(T t);

    void delete(T t);

    void update(T t);
}
