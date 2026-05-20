package pojo;

import java.time.LocalDate;

public class Backdown {
    private Integer id;
    private String remarks;
    private Boolean isDeleted;
    private Integer customerId;
    private LocalDate retreatTime;
    private Integer retreatType;   // 0-正常退住 1-死亡退住 2-保留床位
    private String retreatReason;
    private Integer auditStatus;   // 0-已提交 1-同意 2-拒绝
    private String auditPerson;
    private LocalDate auditTime;

    public Backdown() {}

    public Backdown(Integer id, String remarks, Boolean isDeleted, Integer customerId, LocalDate retreatTime,
                    Integer retreatType, String retreatReason, Integer auditStatus, String auditPerson, LocalDate auditTime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.retreatTime = retreatTime;
        this.retreatType = retreatType;
        this.retreatReason = retreatReason;
        this.auditStatus = auditStatus;
        this.auditPerson = auditPerson;
        this.auditTime = auditTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public LocalDate getRetreatTime() { return retreatTime; }
    public void setRetreatTime(LocalDate retreatTime) { this.retreatTime = retreatTime; }
    public Integer getRetreatType() { return retreatType; }
    public void setRetreatType(Integer retreatType) { this.retreatType = retreatType; }
    public String getRetreatReason() { return retreatReason; }
    public void setRetreatReason(String retreatReason) { this.retreatReason = retreatReason; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }
    public String getAuditPerson() { return auditPerson; }
    public void setAuditPerson(String auditPerson) { this.auditPerson = auditPerson; }
    public LocalDate getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDate auditTime) { this.auditTime = auditTime; }

    @Override
    public String toString() {
        return "Backdown{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", customerId=" + customerId +
                ", retreatTime=" + retreatTime +
                ", retreatType=" + retreatType +
                ", retreatReason='" + retreatReason + '\'' +
                ", auditStatus=" + auditStatus +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditTime=" + auditTime +
                '}';
    }
}