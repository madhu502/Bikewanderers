package com.system.car_rental_system.controller;

import com.system.car_rental_system.entity.Bike;
import com.system.car_rental_system.entity.Booking;
import com.system.car_rental_system.pojo.BikePojo;
import com.system.car_rental_system.pojo.BookingPojo;
import com.system.car_rental_system.services.CarService;
import com.system.car_rental_system.services.BookingService;
import com.system.car_rental_system.services.CategoryService;
import com.system.car_rental_system.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class CarController {
    private final CarService carService;
    private final UserService userService;
    private final BookingService bookingService;
    private final CategoryService categoryService;

    @GetMapping("/bike/{id}")
    public String getBikePage(@PathVariable("id") Integer id, Model model, Principal principal, BookingPojo bookingPojo, Authentication authentication){
        model.addAttribute("loggedUser", userService.findByEmail(principal.getName()));

        Bike bike = carService.fetchById(id);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ADMIN")) {
                model.addAttribute("allCategory", categoryService.fetchAll());
                model.addAttribute("bike", bike);
                return "eachCar";
            }
        }

        model.addAttribute("bike", bike);

        Map<String, List<Booking>> map = bookingService.getBookingHistory(userService.findByEmail(principal.getName()));

        boolean currentBooking = map.get("Current").size()==0 && map.get("Upcoming").size()==0;
        model.addAttribute("notBookedCurrently", currentBooking);

        List<Bike> similarBike = carService.similarBikes(bike.getCategory().getId(), bike.getId());
        model.addAttribute("similarBikes", similarBike);
        model.addAttribute("booking", bookingPojo);

        return "bikeDetails";
    }

    @PostMapping("/bike/save")
    public String saveUser(@Valid BikePojo bikePojo, RedirectAttributes redirectAttributes) throws IOException {
        carService.saveBike(bikePojo);
        redirectAttributes.addFlashAttribute("message", "Bike Updated Successfully!");
        return "redirect:/login";
    }
}
