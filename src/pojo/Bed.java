package pojo;

public class Bed {
    private int id;
    private int roomNo;
    private int bedStatus;
    private String remarks;
    private String bedNo;

    public Bed(){}

    public Bed(int id, int roomNo, int bedStatus, String remarks, String bedNo) {
        this.id = id;
        this.roomNo = roomNo;
        this.bedStatus = bedStatus;
        this.remarks = remarks;
        this.bedNo = bedNo;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getRoomNo() {
        return roomNo;
    }
    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }
    public int getBedStatus() {
        return bedStatus;
    }
    public void setBedStatus(int bedStatus) {
        this.bedStatus = bedStatus;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getBedNo() {
        return bedNo;
    }
    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    @Override
    public String toString() {
        return "Bed{" +
                "id=" + id +
                ", roomNo='" + roomNo + '\'' +
                ", bedStatus='" + bedStatus + '\'' +
                ", remarks='" + remarks + '\'' +
                ", bedNo=" + bedNo +
                '}';
    }

}
