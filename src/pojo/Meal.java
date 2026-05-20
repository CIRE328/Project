package pojo;

public class Meal {
    private Integer id;
    private String weekDay;
    private Integer foodId;
    private Integer mealType;   // 1-早餐 2-午餐 3-晚餐
    private String taste;       // 多糖、多盐、少糖、少盐
    private Boolean isDeleted;

    public Meal() {}

    public Meal(Integer id, String weekDay, Integer foodId, Integer mealType, String taste, Boolean isDeleted) {
        this.id = id;
        this.weekDay = weekDay;
        this.foodId = foodId;
        this.mealType = mealType;
        this.taste = taste;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }
    public Integer getFoodId() { return foodId; }
    public void setFoodId(Integer foodId) { this.foodId = foodId; }
    public Integer getMealType() { return mealType; }
    public void setMealType(Integer mealType) { this.mealType = mealType; }
    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", weekDay='" + weekDay + '\'' +
                ", foodId=" + foodId +
                ", mealType=" + mealType +
                ", taste='" + taste + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}