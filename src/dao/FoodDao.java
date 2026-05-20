package dao;

import pojo.Food;

public class FoodDao extends BaseAbstractDao<Food> {
    public FoodDao() {
        super("foods.json", Food.class);
    }
}