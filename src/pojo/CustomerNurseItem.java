package pojo;

import java.time.LocalDate;

public class CustomerNurseItem {
    private Integer id;
    private Integer itemId;
    private Integer customerId;
    private Integer levelId;
    private Integer nurseNumber;
    private Boolean isDeleted;
    private LocalDate buyTime;
    private LocalDate maturityTime;

    public CustomerNurseItem() {}

    public CustomerNurseItem(Integer id, Integer itemId, Integer customerId, Integer levelId,
                             Integer nurseNumber, Boolean isDeleted, LocalDate buyTime, LocalDate maturityTime) {
        this.id = id;
        this.itemId = itemId;
        this.customerId = customerId;
        this.levelId = levelId;
        this.nurseNumber = nurseNumber;
        this.isDeleted = isDeleted;
        this.buyTime = buyTime;
        this.maturityTime = maturityTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public Integer getNurseNumber() { return nurseNumber; }
    public void setNurseNumber(Integer nurseNumber) { this.nurseNumber = nurseNumber; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public LocalDate getBuyTime() { return buyTime; }
    public void setBuyTime(LocalDate buyTime) { this.buyTime = buyTime; }
    public LocalDate getMaturityTime() { return maturityTime; }
    public void setMaturityTime(LocalDate maturityTime) { this.maturityTime = maturityTime; }

    @Override
    public String toString() {
        return "CustomerNurseItem{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", customerId=" + customerId +
                ", levelId=" + levelId +
                ", nurseNumber=" + nurseNumber +
                ", isDeleted=" + isDeleted +
                ", buyTime=" + buyTime +
                ", maturityTime=" + maturityTime +
                '}';
    }
}