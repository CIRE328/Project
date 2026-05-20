package dao;

import java.util.*;

/**
 * 通用数据访问接口
 * @param <T>  实体类型
 * @param <ID> 主键类型（一般为 Integer）
 */
public interface BaseDao<T, ID> {

    /**
     * 查询所有未删除的数据
     */
    List<T> findAll();

    /**
     * 根据ID查询未删除的数据
     */
    Optional<T> findById(ID id);

    /**
     * 插入一条新数据（自动生成ID）
     */
    T insert(T entity);

    /**
     * 更新一条数据（根据ID）
     */
    T update(T entity);

    /**
     * 逻辑删除（设置 isDeleted = true）
     * @param id 主键
     * @return 是否删除成功
     */
    boolean deleteById(ID id);

    /**
     * 物理删除（直接从文件移除，慎用）
     */
    boolean forceDeleteById(ID id);

    /**
     * 查询所有数据（包括已删除）
     */
    List<T> findAllIncludingDeleted();
}