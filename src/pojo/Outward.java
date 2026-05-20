package pojo;

import java.time.LocalDate;

public class Outward {
    private Integer id;
    private String remarks;
    private Boolean isDeleted;
    private Integer customerId;
    private String outgoingReason;
    private LocalDate outgoingTime;
    private LocalDate expectedReturnTime;
    private LocalDate actualReturnTime;
    private String escorted;
    private String relation;
    private String escortedTel;
    private Integer auditStatus;   // 0-已提交 1-同意 2-拒绝
    private String auditPerson;
    private LocalDate auditTime;

    public Outward() {}

    public Outward(Integer id, String remarks, Boolean isDeleted, Integer customerId, String outgoingReason,
                   LocalDate outgoingTime, LocalDate expectedReturnTime, LocalDate actualReturnTime,
                   String escorted, String relation, String escortedTel, Integer auditStatus,
                   String auditPerson, LocalDate auditTime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.outgoingReason = outgoingReason;
        this.outgoingTime = outgoingTime;
        this.expectedReturnTime = expectedReturnTime;
        this.actualReturnTime = actualReturnTime;
        this.escorted = escorted;
        this.relation = relation;
        this.escortedTel = escortedTel;
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
    public String getOutgoingReason() { return outgoingReason; }
    public void setOutgoingReason(String outgoingReason) { this.outgoingReason = outgoingReason; }
    public LocalDate getOutgoingTime() { return outgoingTime; }
    public void setOutgoingTime(LocalDate outgoingTime) { this.outgoingTime = outgoingTime; }
    public LocalDate getExpectedReturnTime() { return expectedReturnTime; }
    public void setExpectedReturnTime(LocalDate expectedReturnTime) { this.expectedReturnTime = expectedReturnTime; }
    public LocalDate getActualReturnTime() { return actualReturnTime; }
    public void setActualReturnTime(LocalDate actualReturnTime) { this.actualReturnTime = actualReturnTime; }
    public String getEscorted() { return escorted; }
    public void setEscorted(String escorted) { this.escorted = escorted; }
    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }
    public String getEscortedTel() { return escortedTel; }
    public void setEscortedTel(String escortedTel) { this.escortedTel = escortedTel; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }
    public String getAuditPerson() { return auditPerson; }
    public void setAuditPerson(String auditPerson) { this.auditPerson = auditPerson; }
    public LocalDate getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDate auditTime) { this.auditTime = auditTime; }

    @Override
    public String toString() {
        return "Outward{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", customerId=" + customerId +
                ", outgoingReason='" + outgoingReason + '\'' +
                ", outgoingTime=" + outgoingTime +
                ", expectedReturnTime=" + expectedReturnTime +
                ", actualReturnTime=" + actualReturnTime +
                ", escorted='" + escorted + '\'' +
                ", relation='" + relation + '\'' +
                ", escortedTel='" + escortedTel + '\'' +
                ", auditStatus=" + auditStatus +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditTime=" + auditTime +
                '}';
    }
}