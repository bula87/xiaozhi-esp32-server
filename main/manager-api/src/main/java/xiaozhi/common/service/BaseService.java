package xiaozhi.common.service;

import java.io.Serializable;
import java.util.Collection;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * Basic service interface, all Service interfaces must inherit from it.
 * Copyright (c)人人开源 All rights reserved.
 * Website: https://www.renren.io
 */
public interface BaseService<T> {
    Class<T> currentModelClass();

    /**
     * <p>
     * Insert a record (selective fields, strategy insertion)
     * </p>
     *
     * @param entity Entity object
     */
    boolean insert(T entity);

    /**
     * <p>
     * Batch insert, this method does not support Oracle, SQL Server
     * </p>
     *
     * @param entityList Collection of entity objects
     */
    boolean insertBatch(Collection<T> entityList);

    /**
     * <p>
     * Batch insert, this method does not support Oracle, SQL Server
     * </p>
     *
     * @param entityList Collection of entity objects
     * @param batchSize  Insertion batch size
     */
    boolean insertBatch(Collection<T> entityList, int batchSize);

    /**
     * <p>
     * Update by ID (selective fields)
     * </p>
     *
     * @param entity Entity object
     */
    boolean updateById(T entity);

    /**
     * <p>
     * Update records based on whereEntity conditions
     * </p>
     *
     * @param entity        Entity object
     * @param updateWrapper Entity object encapsulation operation class
     *                      {@link com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper}
     */
    boolean update(T entity, Wrapper<T> updateWrapper);

    /**
     * <p>
     * Batch update by ID
     * </p>
     *
     * @param entityList Collection of entity objects
     */
    boolean updateBatchById(Collection<T> entityList);

    /**
     * <p>
     * Batch update by ID
     * </p>
     *
     * @param entityList Collection of entity objects
     * @param batchSize  Update batch size
     */
    boolean updateBatchById(Collection<T> entityList, int batchSize);

    /**
     * <p>
     * Query by ID
     * </p>
     *
     * @param id Primary key ID
     */
    T selectById(Serializable id);

    /**
     * <p>
     * Delete by ID
     * </p>
     *
     * @param id Primary key ID
     */
    boolean deleteById(Serializable id);

    /**
     * <p>
     * Batch delete (based on primary key IDs)
     * </p>
     *
     * @param idList List of primary key IDs
     */
    boolean deleteBatchIds(Collection<? extends Serializable> idList);
}
 