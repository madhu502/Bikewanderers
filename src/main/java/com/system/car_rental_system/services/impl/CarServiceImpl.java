package com.system.car_rental_system.services.impl;

import com.system.car_rental_system.entity.Bike;
import com.system.car_rental_system.pojo.BikePojo;
import com.system.car_rental_system.repo.BikeRepo;
import com.system.car_rental_system.repo.CategoryRepo;
import com.system.car_rental_system.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepo carRepo;

    private final CategoryRepo categoryRepo;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\cars\\";

    @Override
    public String saveBike(CarPojo carPojo) throws IOException {
        Car car;
        try {
            car = carRepo.findById(carPojo.getId()).orElseThrow();
        } catch (Exception e){
            car = new Bike();
        }
        car.setCarName(carPojo.getBikeName());
        car.setBrandName(carPojo.getBrandName());
        car.setAvailableNo(carPojo.getAvailableNo());

        if(!Objects.equals(carPojo.getBikeImage().getOriginalFilename(), "")){
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, carPojo.getCarImage().getOriginalFilename());
            Files.write(fileNameAndPath, carPojo.getBikeImage().getBytes());

            car.setCarImage(carPojo.getCarImage().getOriginalFilename());
        }

        car.setPriceDay(carPojo.getPriceDay());
        car.setMileage(carPojo.getMileage());
        car.setMaxTorque(carPojo.getMaxTorque());
        car.setPower(carPojo.getPower());
        car.setTankCapacity(carbikePojo.getTankCapacity());
        car.setTopSpeed(carPojo.getTopSpeed());
        car.setTransmission(carPojo.getTransmission());
        car.setCategory(categoryRepo.findById(carPojo.getCategory()).orElseThrow());
        carRepo.save(car);
        return "Car Saved";
    }

    @Override
    public String saveCarByEntity(Car car){
        carRepo.save(car);
        return "Updated";
    }

    @Override
    public Car fetchById(Integer id) {
        Car car = carRepo.findById(id).orElseThrow(()->new RuntimeException("Not Found"));

        car = Car.builder()
                .id(car.getId())
                .bikeName(car.getBikeName())
                .brandName(car.getBrandName())
                .availableNo(car.getAvailableNo())
                .rentedNumber(car.getRentedNumber())
                .bikeImage(car.getCarImage())
                .bikeImageBase64(getImageBase64(car.getCarImage()))
                .priceDay(car.getPriceDay())
                .mileage(car.getMileage())
                .maxTorque(car.getMaxTorque())
                .power(car.getPower())
                .tankCapacity(car.getTankCapacity())
                .topSpeed(car.getTopSpeed())
                .transmission(car.getTransmission())
                .category(car.getCategory())
                .build();

        return car;
    }

    @Override
    public List<Car> fetchByCategory(Integer categoryId) {
        List<Bike> allCar = carRepo.findAllByCategory(categoryId).orElseThrow(()->new RuntimeException("Not Found"));

        allCars=listMapping(allCars);

        List<Car> requiredBikes = new ArrayList<>();
        for (int i=0; i<8 && i<allBikes.size(); i++){
            requiredBikes.add(allBikes.get(i));
        }
        return requiredBikes;
    }

    @Override
    public List<Bike> similarBikes(Integer categoryId, Integer bikeId) {
        List<Bike> allBikes = bikeRepo.findAllByCategory(categoryId).orElseThrow(()->new RuntimeException("Not Found"));

        for (int i=0; i<allBikes.size(); i++){
            if (Objects.equals(allBikes.get(i).getId(), bikeId)){
                allBikes.remove(i);
                break;
            }
        }

        Collections.shuffle(allBikes);

        allBikes=listMapping(allBikes);

        List<Bike> requiredBikes = new ArrayList<>();
        for (int i=0; i<4 && i<allBikes.size(); i++){
            requiredBikes.add(allBikes.get(i));
        }
        return requiredBikes;
    }

    @Override
    public List<Bike> fetchAllByCategory(Integer categoryId) {
        return listMapping(bikeRepo.findAllByCategory(categoryId).orElseThrow(()->new RuntimeException("Not Found")));
    }

    @Override
    public List<Bike> fetchMostRented() {
        return listMapping(bikeRepo.findMostRented().orElseThrow(()->new RuntimeException("Not Found")));
    }

    public List<Bike> listMapping(List<Bike> list){
        Stream<Bike> allBikesWithImage=list.stream().map(bike ->
                Bike.builder()
                        .id(bike.getId())
                        .bikeName(bike.getBikeName())
                        .brandName(bike.getBrandName())
                        .availableNo(bike.getAvailableNo())
                        .rentedNumber(bike.getRentedNumber())
                        .bikeImageBase64(getImageBase64(bike.getBikeImage()))
                        .priceDay(bike.getPriceDay())
                        .mileage(bike.getMileage())
                        .maxTorque(bike.getMaxTorque())
                        .power(bike.getPower())
                        .tankCapacity(bike.getTankCapacity())
                        .topSpeed(bike.getTopSpeed())
                        .transmission(bike.getTransmission())
                        .category(bike.getCategory())
                        .build()
        );
        list = allBikesWithImage.toList();
        return list;
    }

    public String getImageBase64(String fileName) {
        if (fileName!=null) {
            String filePath = System.getProperty("user.dir")+"/images/bikes/";
            File file = new File(filePath + fileName);
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
}
