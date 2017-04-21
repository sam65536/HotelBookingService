package com.geekhub.controllers;

import com.geekhub.domain.CustomUserDetails;
import com.geekhub.domain.UserRole;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForCommentModerator;
import com.geekhub.services.City.CityService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

    private final HotelService hotelService;
    private final UserService userService;
    private final CityService cityService;

    @Autowired
    public ApplicationController(HotelService hotelService, UserService userService, CityService cityService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.cityService = cityService;
    }

    @RequestMapping(value = "/")
    public String root(Model model) {
        model.addAttribute("hotels", hotelService.findAll());
        model.addAttribute("cities", cityService.findAll());
        return "landing-page";
    }

    @RequestMapping(value = "/signedin")
    public String signedIn(Authentication authentication) {
        CustomUserDetails principal = (authentication != null) ? (CustomUserDetails) authentication.getPrincipal() : null;
        if (principal != null) {
            String authority = principal.getAuthorities().iterator().next().getAuthority();
            switch (UserRole.valueOf(authority)) {
                case ROLE_ADMIN:
                    return "redirect:/admin";
                case ROLE_COMMENT_MODERATOR:
                    return "redirect:/comments/moderation";
                case ROLE_USER:
                    return "redirect:/users/me";
                case ROLE_HOTEL_MANAGER:
                    return "redirect:/users/me";
            }
        }
        return "/";
    }

   @RequestMapping(value = "/comments/moderation")
   @AllowedForCommentModerator
   public String moderateComments(Model model) {
       model.addAttribute("hotels", hotelService.findAll());
       return "comments/comment-moderating";
   }
	
   @RequestMapping(value = "/admin")
   @AllowedForAdmin
   public String manageUsers(Model model) {
       model.addAttribute("users", userService.findAll());
       model.addAttribute("hotels", hotelService.findAll());
       return "admin-dashboard";
   }
}