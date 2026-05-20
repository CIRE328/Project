package dao;

import pojo.Meal;

public class MealDao extends BaseAbstractDao<Meal> {
    public MealDao() {
        super("meals.json", Meal.class);
    }
}
