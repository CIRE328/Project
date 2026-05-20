package pojo;

import java.time.LocalDateTime;

public class Role {
    private Integer id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer updateBy;
    private Boolean isDeleted;
    private String name;

    public Role() {}

    public Role(Integer id, LocalDateTime createTime, LocalDateTime updateTime, Integer updateBy,
                Boolean isDeleted, String name) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.isDeleted = isDeleted;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getUpdateBy() { return updateBy; }
    public void setUpdateBy(Integer updateBy) { this.updateBy = updateBy; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", updateBy=" + updateBy +
                ", isDeleted=" + isDeleted +
                ", name='" + name + '\'' +
                '}';
    }
}