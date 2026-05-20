package dao;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.lang.reflect.Field;
import tools.JsonFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * 抽象 DAO 基类，提供基于 JSON 文件的通用 CRUD 实现
 * @param <T>  实体类型（必须有 Integer 类型的 id 字段和 Boolean/Integer 类型的 isDeleted 字段）
 */

public class BaseAbstractDao<T> implements BaseDao<T, Integer> {

    private final String filePath;
    private final Class<T> clazz;
    private final ObjectMapper mapper;

    /**
     * 构造函数
     * @param fileName JSON 文件名（如 "customers.json"）
     * @param clazz    实体类的 Class 对象
     */
    public BaseAbstractDao(String fileName, Class<T> clazz) {
        this.filePath = "data/" + fileName;
        this.clazz = clazz;
        this.mapper = JsonFileUtil.getObjectMapper(); // 需要在 JsonFileUtil 中暴露 mapper 对象
    }

    // ========== 文件读写基础方法 ==========

    /**
     * 从文件读取所有数据（包含已删除）
     */
    protected List<T> readAll() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            CollectionType listType = mapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, clazz);
            return mapper.readValue(file, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 将全部数据写入文件
     */
    protected void writeAll(List<T> list) {
        File file = new File(filePath);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成下一个可用的 ID（取当前最大 id + 1）
     */
    protected Integer generateNextId(List<T> list) {
        int maxId = 0;
        for (T entity : list) {
            try {
                Field idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                Integer id = (Integer) idField.get(entity);
                if (id != null && id > maxId) {
                    maxId = id;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return maxId + 1;
    }

    /**
     * 设置实体的 id 字段值
     */
    protected void setId(T entity, Integer id) {
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取实体的 id 值
     */
    protected Integer getId(T entity) {
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            return (Integer) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取实体的 isDeleted 字段值（假设字段类型为 Boolean）
     * 如果字段是 Integer（0/1），需子类重写或修改此处逻辑
     */
    protected boolean getIsDeleted(T entity) {
        try {
            Field deletedField = clazz.getDeclaredField("isDeleted");
            deletedField.setAccessible(true);
            Object value = deletedField.get(entity);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof Integer) {
                return (Integer) value == 1;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 如果没有 isDeleted 字段，认为未删除
            return false;
        }
        return false;
    }

    /**
     * 设置实体的 isDeleted 字段值（Boolean 类型）
     */
    protected void setIsDeleted(T entity, boolean deleted) {
        try {
            Field deletedField = clazz.getDeclaredField("isDeleted");
            deletedField.setAccessible(true);
            deletedField.set(entity, deleted);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // ========== BaseDao 接口实现 ==========

    @Override
    public List<T> findAll() {
        return readAll().stream()
                .filter(e -> !getIsDeleted(e))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(Integer id) {
        return findAll().stream()
                .filter(e -> id.equals(getId(e)))
                .findFirst();
    }

    @Override
    public T insert(T entity) {
        List<T> list = readAll();
        Integer newId = generateNextId(list);
        setId(entity, newId);
        setIsDeleted(entity, false);
        list.add(entity);
        writeAll(list);
        return entity;
    }

    @Override
    public T update(T entity) {
        List<T> list = readAll();
        Integer id = getId(entity);
        if (id == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            T existing = list.get(i);
            if (id.equals(getId(existing))) {
                list.set(i, entity);
                writeAll(list);
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(Integer id) {
        List<T> list = readAll();
        for (T entity : list) {
            if (id.equals(getId(entity))) {
                setIsDeleted(entity, true);
                writeAll(list);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean forceDeleteById(Integer id) {
        List<T> list = readAll();
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(getId(list.get(i)))) {
                list.remove(i);
                writeAll(list);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return readAll();
    }
}
