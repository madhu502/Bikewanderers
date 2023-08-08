package com.system.car_rental_system.services;

import com.system.car_rental_system.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> fetchAll();
    Category fetchById(Integer id);
}
