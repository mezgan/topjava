package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = caloriesByDay(meals);
        List<UserMealWithExcess> result = new ArrayList<>();

        for (UserMeal userMeal : meals) {
            LocalTime mealTime = userMeal.getDateTime().toLocalTime();
            if (TimeUtil.isBetweenHalfOpen(mealTime, startTime, endTime)) {
                boolean excess = excess(caloriesByDay, userMeal, caloriesPerDay);
                result.add(toUserMealWithExcess(userMeal, excess));
            }
        }
        return result;
    }

    private static boolean excess(Map<LocalDate, Integer> caloriesByDay, UserMeal userMeal, int caloriesPerDay) {
        boolean excess = false;
        LocalDate mealDate = userMeal.getDateTime().toLocalDate();
        if (caloriesByDay.get(mealDate) > caloriesPerDay) {
            excess = true;
        }
        return excess;
    }

    private static Map<LocalDate, Integer> caloriesByDay(List<UserMeal> meals) {
        Map<LocalDate, Integer> caloriesByDay = new CaloriesByDay();
        for (UserMeal userMeal : meals) {
            LocalDate date = userMeal.getDateTime().toLocalDate();
            int calories = userMeal.getCalories();
            caloriesByDay.put(date, calories);
        }
        return caloriesByDay;
    }

    private static UserMealWithExcess toUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(
                userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                excess);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        return null;
    }

    private static class CaloriesByDay extends HashMap<LocalDate, Integer> {
        @Override
        public Integer put(LocalDate key, Integer value) {
            Integer calories = get(key);
            return super.put(key, value + calories);
        }

        @Override
        public Integer get(Object key) {
            Integer value = super.get(key);
            return value == null ? 0 : value;
        }
    }
}
