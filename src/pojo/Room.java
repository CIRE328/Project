package pojo;

public class Room {
    private Integer id;
    private String roomFloor;
    private Integer roomNo;
    private Boolean isDeleted;

    public Room() {}

    public Room(Integer id, String roomFloor, Integer roomNo, Boolean isDeleted) {
        this.id = id;
        this.roomFloor = roomFloor;
        this.roomNo = roomNo;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRoomFloor() { return roomFloor; }
    public void setRoomFloor(String roomFloor) { this.roomFloor = roomFloor; }
    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomFloor='" + roomFloor + '\'' +
                ", roomNo=" + roomNo +
                ", isDeleted=" + isDeleted +
                '}';
    }
}