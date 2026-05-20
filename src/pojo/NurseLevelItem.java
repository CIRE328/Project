package pojo;

public class NurseLevelItem {
    private Integer id;
    private Integer levelId;
    private Integer itemId;
    private Boolean isDeleted;

    public NurseLevelItem() {}

    public NurseLevelItem(Integer id, Integer levelId, Integer itemId, Boolean isDeleted) {
        this.id = id;
        this.levelId = levelId;
        this.itemId = itemId;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "NurseLevelItem{" +
                "id=" + id +
                ", levelId=" + levelId +
                ", itemId=" + itemId +
                ", isDeleted=" + isDeleted +
                '}';
    }
}