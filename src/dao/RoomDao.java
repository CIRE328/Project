package dao;

import pojo.Room;

public class RoomDao extends BaseAbstractDao<Room> {
    public RoomDao() {
        super("rooms.json", Room.class);
    }
}