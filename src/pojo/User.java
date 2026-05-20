package pojo;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String nickname;
    private String username;
    private String password;
    private Integer sex;
    private String email;
    private String phoneNumber;
    private Integer roleId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDeleted;
    private Integer createBy;
    private Integer updateBy;

    public User(){}

    public User(Integer id, String nickname, String username, String password, Integer sex, String email,
                String phoneNumber, Integer roleId, LocalDateTime createTime, Integer createBy,
                LocalDateTime updateTime, Integer updateBy, Boolean isDeleted) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roleId = roleId;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getSex() {
        return sex;
    }
    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Integer getRoleId() {
        return roleId;
    }
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public Integer getCreateBy() {
        return createBy;
    }
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getUpdateBy() {
        return updateBy;
    }
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", roleId=" + roleId +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                ", updateTime=" + updateTime +
                ", updateBy=" + updateBy +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
