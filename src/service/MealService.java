package service;

import dao.*;
import pojo.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 膳食管理业务逻辑
 * 包含：食品管理、客户饮食喜好管理、膳食日历管理
 */
public class MealService {

    private final FoodDao foodDao = new FoodDao();
    private final CustomerPreferenceDao preferenceDao = new CustomerPreferenceDao();
    private final MealDao mealDao = new MealDao();
    private final CustomerDao customerDao = new CustomerDao();

    // ==================== 食品管理 ====================

    /**
     * 查询所有未删除的食品
     */
    public List<Food> findAllFoods() {
        return foodDao.findAll();
    }

    /**
     * 根据名称模糊查询食品
     */
    public List<Food> findFoodsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllFoods();
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodName() != null && f.getFoodName().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 根据类型查询食品（如 "主食","汤品" 等）
     */
    public List<Food> findFoodsByType(String foodType) {
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodType() != null && f.getFoodType().equals(foodType))
                .collect(Collectors.toList());
    }

    /**
     * 添加食品
     */
    public Food addFood(Food food) {
        // 可添加校验：名称不重复等
        return foodDao.insert(food);
    }

    /**
     * 更新食品
     */
    public boolean updateFood(Food food) {
        if (foodDao.findById(food.getId()).isEmpty()) return false;
        foodDao.update(food);
        return true;
    }

    /**
     * 逻辑删除食品
     */
    public boolean deleteFood(Integer foodId) {
        return foodDao.deleteById(foodId);
    }

    // ==================== 客户饮食喜好管理 ====================

    /**
     * 查询客户的饮食喜好记录（一个客户应该只有一条）
     */
    public Optional<CustomerPreference> findPreferenceByCustomerId(Integer customerId) {
        return preferenceDao.findAll().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .findFirst();
    }

    /**
     * 添加或更新客户饮食喜好
     */
    public boolean savePreference(CustomerPreference preference) {
        // 检查客户是否存在
        if (customerDao.findById(preference.getCustomerId()).isEmpty()) return false;
        // 如果已有记录，则更新；否则插入
        Optional<CustomerPreference> existing = findPreferenceByCustomerId(preference.getCustomerId());
        if (existing.isPresent()) {
            preference.setId(existing.get().getId());
            preferenceDao.update(preference);
        } else {
            preferenceDao.insert(preference);
        }
        return true;
    }

    /**
     * 删除客户饮食喜好
     */
    public boolean deletePreference(Integer customerId) {
        Optional<CustomerPreference> opt = findPreferenceByCustomerId(customerId);
        return opt.filter(preference -> preferenceDao.deleteById(preference.getId())).isPresent();
    }

    // ==================== 膳食日历管理 ====================

    /**
     * 查询某天的所有餐次安排
     * @param weekDay 周一~周日 (字符串)
     */
    public List<Meal> findMealsByWeekDay(String weekDay) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay))
                .collect(Collectors.toList());
    }

    /**
     * 查询某天某餐次的食品
     */
    public Optional<Meal> findMeal(String weekDay, Integer mealType) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay) && m.getMealType().equals(mealType))
                .findFirst();
    }

    /**
     * 为某天某餐次安排食品
     * @param meal 若id为null则新增，否则更新
     */
    public boolean scheduleMeal(Meal meal) {
        // 校验食品存在
        if (meal.getFoodId() != null && foodDao.findById(meal.getFoodId()).isEmpty()) {
            return false;
        }
        if (meal.getId() == null) {
            mealDao.insert(meal);
        } else {
            mealDao.update(meal);
        }
        return true;
    }

    /**
     * 移除某天某餐次的安排
     */
    public boolean removeMealSchedule(String weekDay, Integer mealType) {
        Optional<Meal> opt = findMeal(weekDay, mealType);
        return opt.filter(meal -> mealDao.deleteById(meal.getId())).isPresent();
    }

    /**
     * 根据客户喜好，推荐本周某一天的餐品（示例：返回该天所有餐次的推荐食品列表）
     * 实际项目中可更复杂，这里仅演示根据客户偏好（如“少糖”）筛选食品
     */
    public List<Meal> recommendMealsForCustomer(Integer customerId, String weekDay) {
        // 获取客户偏好
        Optional<CustomerPreference> prefOpt = findPreferenceByCustomerId(customerId);
        String preferenceTag = prefOpt.map(CustomerPreference::getPreferences).orElse("");
        // 获取当天的当前安排
        List<Meal> currentMeals = findMealsByWeekDay(weekDay);
        // 这里简单返回当前安排，实际可根据 preferenceTag 调整食品
        // 例如若偏好“少糖”，则过滤掉含糖高的食品，但需要食品表有额外字段，暂不实现
        return currentMeals;
    }
}