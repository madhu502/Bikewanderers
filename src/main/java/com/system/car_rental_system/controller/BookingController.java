package com.system.car_rental_system.controller;

import com.system.car_rental_system.entity.Bike;
import com.system.car_rental_system.entity.Booking;
import com.system.car_rental_system.entity.User;
import com.system.car_rental_system.pojo.BookingPojo;
import com.system.car_rental_system.services.CarService;
import com.system.car_rental_system.services.BookingService;
import com.system.car_rental_system.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class BookingController {
    private final BookingService bookingService;

    private final UserService userService;

    private final CarService carService;

    @PostMapping("/book/{bikeId}")
    public String bookBike(@PathVariable Integer bikeId, @Valid BookingPojo bookingPojo, Principal principal, RedirectAttributes redirectAttributes) throws ParseException {
        Integer id = userService.findByEmail(principal.getName()).getId();
        bookingPojo.setUserId(id);
        bookingPojo.setBikeId(bikeId);
        bookingPojo.setStatus("Booked");
        bookingService.saveBooking(bookingPojo);

        Bike updatedBike = carService.fetchById(bikeId);
        updatedBike.setAvailableNo(updatedBike.getAvailableNo() - 1);
        carService.saveBikeByEntity(updatedBike);

        redirectAttributes.addFlashAttribute("message", "Your booking was placed. Visit us at (some place) to collect your bike on the selected date.") ;
        return "redirect:/home";
    }

    @GetMapping("/my-bookings")
    public String getBookedHistory(Principal principal, Model model){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("loggedUser", user);
        model.addAttribute("reservations", bookingService.getBookingHistory(user));
        return "/myReservations";
    }

    @GetMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes, Authentication authentication){
        Bike updatedBike = carService.fetchById(bookingService.findById(id).getBikeId().getId());
        updatedBike.setAvailableNo(updatedBike.getAvailableNo() + 1);
        carService.saveBikeByEntity(updatedBike);

        bookingService.deleteBooking(id);

        redirectAttributes.addFlashAttribute("errorMessage", "Your Booking was Cancelled!");
        return "redirect:/my-bookings";
    }

    @GetMapping("booking/{id}/{status}")
    public String updateStatus(@PathVariable Integer id, @PathVariable String status) {
        Booking booking = bookingService.findById(id);
        booking.setStatus(status);
        bookingService.saveBookingByEntity(booking);

        if (Objects.equals(status, "Taken")){
            Bike updatedBike = carService.fetchById(booking.getBikeId().getId());
            updatedBike.setRentedNumber(updatedBike.getRentedNumber() + 1);
            carService.saveBikeByEntity(updatedBike);
        }

        if (Objects.equals(status, "Returned")){
            Bike updatedBike = carService.fetchById(booking.getBikeId().getId());
            updatedBike.setAvailableNo(updatedBike.getAvailableNo() + 1);
            carService.saveBikeByEntity(updatedBike);
        }

        return "redirect:/bookings/"+LocalDate.now();
    }
}
