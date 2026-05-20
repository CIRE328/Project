package pojo;

public class RoleMenu {
    private Integer id;
    private Integer roleId;
    private Integer menu;
    private Boolean isDeleted;

    public RoleMenu() {}

    public RoleMenu(Integer id, Integer roleId, Integer menu, Boolean isDeleted) {
        this.id = id;
        this.roleId = roleId;
        this.menu = menu;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public Integer getMenu() { return menu; }
    public void setMenu(Integer menu) { this.menu = menu; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "RoleMenu{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", menu=" + menu +
                ", isDeleted=" + isDeleted +
                '}';
    }
}