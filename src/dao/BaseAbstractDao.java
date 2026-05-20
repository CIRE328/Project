package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 抽象 DAO 基类，提供基于 JSON 文件的通用 CRUD 实现
 * 内部使用配置好的 ObjectMapper 支持 Java 8 时间类型
 *
 * @param <T> 实体类型（必须有 Integer id 和 Boolean isDeleted）
 */
public abstract class BaseAbstractDao<T> implements BaseDao<T, Integer> {

    // 配置 ObjectMapper：支持 LocalDate/LocalDateTime，禁用时间戳格式
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String DATA_DIR = "data/";

    private final String filePath;
    private final Class<T> clazz;

    /**
     * 构造函数
     *
     * @param fileName JSON 文件名（如 "customers.json"）
     * @param clazz    实体类的 Class 对象
     */
    public BaseAbstractDao(String fileName, Class<T> clazz) {
        this.filePath = DATA_DIR + fileName;
        this.clazz = clazz;
        // 确保 data 目录存在
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
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
            CollectionType listType = MAPPER.getTypeFactory()
                    .constructCollectionType(ArrayList.class, clazz);
            return MAPPER.readValue(file, listType);
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
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== 反射操作 id 和 isDeleted 字段 ==========

    private Integer generateNextId(List<T> list) {
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
                // 忽略，实体可能没有 id 字段（但根据规范应该有）
            }
        }
        return maxId + 1;
    }

    private void setId(T entity, Integer id) {
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Integer getId(T entity) {
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            return (Integer) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getIsDeleted(T entity) {
        try {
            Field field = clazz.getDeclaredField("isDeleted");
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof Integer) {
                return (Integer) value == 1;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 没有 isDeleted 字段，视为未删除
        }
        return false;
    }

    private void setIsDeleted(T entity, boolean deleted) {
        try {
            Field field = clazz.getDeclaredField("isDeleted");
            field.setAccessible(true);
            field.set(entity, deleted);
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