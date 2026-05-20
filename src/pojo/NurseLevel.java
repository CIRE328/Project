package pojo;

public class NurseLevel {
    private Integer id;
    private String levelName;
    private Integer levelStatus;   // 1-启用 2-停用
    private Boolean isDeleted;

    public NurseLevel() {}

    public NurseLevel(Integer id, String levelName, Integer levelStatus, Boolean isDeleted) {
        this.id = id;
        this.levelName = levelName;
        this.levelStatus = levelStatus;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public Integer getLevelStatus() { return levelStatus; }
    public void setLevelStatus(Integer levelStatus) { this.levelStatus = levelStatus; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "NurseLevel{" +
                "id=" + id +
                ", levelName='" + levelName + '\'' +
                ", levelStatus=" + levelStatus +
                ", isDeleted=" + isDeleted +
                '}';
    }
}