package com.system.car_rental_system.services;

import com.system.car_rental_system.entity.Bike;
import com.system.car_rental_system.pojo.BikePojo;

import java.io.IOException;
import java.util.List;

public interface CarService {
    String saveCar(CarPojo carPojo) throws IOException;
    String saveCarByEntity(Car car);
    Car fetchById(Integer id);
    List<Car> fetchByCategory(Integer categoryId);
    List<Car> similarBikes(Integer categoryId, Integer carId);
    List<Car> fetchAllByCategory(Integer categoryId);
    List<Car> fetchMostRented();
}
