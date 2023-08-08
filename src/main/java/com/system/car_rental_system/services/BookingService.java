package com.system.car_rental_system.services;

import com.system.car_rental_system.entity.Booking;
import com.system.car_rental_system.entity.User;
import com.system.car_rental_system.pojo.BookingPojo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BookingService {

    void saveBooking(BookingPojo bookingPojo) throws ParseException;

    void saveBookingByEntity(Booking booking);

    List<Booking> fetchAll(String date);

    String deleteBooking(Integer id);

    Booking findById(Integer id);

    Map<String, List<Booking>> getBookingHistory(User user);
}
