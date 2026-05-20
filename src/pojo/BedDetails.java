package pojo;

import java.time.LocalDate;

public class BedDetails {
    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String bedDetails;
    private Integer customerId;
    private Integer bedId;
    private Boolean isDeleted;

    public BedDetails() {}

    public BedDetails(Integer id, LocalDate startDate, LocalDate endDate, String bedDetails,
                      Integer customerId, Integer bedId, Boolean isDeleted) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bedDetails = bedDetails;
        this.customerId = customerId;
        this.bedId = bedId;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getBedDetails() { return bedDetails; }
    public void setBedDetails(String bedDetails) { this.bedDetails = bedDetails; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "BedDetails{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bedDetails='" + bedDetails + '\'' +
                ", customerId=" + customerId +
                ", bedId=" + bedId +
                ", isDeleted=" + isDeleted +
                '}';
    }
}