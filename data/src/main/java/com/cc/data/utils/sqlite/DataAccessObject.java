package com.cc.data.utils.sqlite;

import java.util.Collection;

/**
 * Author: NT
 * Since: 10/24/2016.
 * Generic Interface Data Access Object
 * CRUD database
 */
public interface DataAccessObject<T> {
    T create(T dto) throws Exception;

    T read(T dto) throws Exception;

    T read(T dto, String whereClause) throws Exception;

    Collection<T> readAll() throws Exception;

    Collection<T> readAll(String whereClause) throws Exception;

    int update(T dto) throws Exception;

    boolean delete(T dto) throws Exception;

    int deleteAll() throws Exception;

}
