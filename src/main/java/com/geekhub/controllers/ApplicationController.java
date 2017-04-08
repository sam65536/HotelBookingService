package com.geekhub.controllers;

import com.geekhub.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.geekhub.domain.CustomUserDetail;
import com.geekhub.repositories.HotelRepository;
import com.geekhub.repositories.UserRepository;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForCommentModerator;

@Controller
public class ApplicationController {

   private final HotelRepository hotels;
   private final UserRepository users;
   private final CityRepository cities;

   @Autowired
   public ApplicationController(HotelRepository hotels, UserRepository users, CityRepository cities) {
       this.hotels = hotels;
       this.users = users;
       this.cities = cities;
   }

   @RequestMapping(value = "/")
   public String root(Model model) {
       model.addAttribute("hotels", cities.findOne((long) 1).getHotels().values());
       model.addAttribute("cities", cities.findAll());
       return "landing-page";
   }
	
   @RequestMapping(value = "/angular")
   public String angular() {
       return "angular-interface";
   }
	
   @RequestMapping(value = "/signedin")
   public String signedIn(Model model, Authentication authentication) {
       CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
       if (principal != null) {
           String authority = principal.getAuthorities().iterator().next().getAuthority();
           if (authority.equals(("ROLE_ADMIN"))) {
              return "redirect:/admin";
           } else if (authority.equals("ROLE_COMMENT_MODERATOR")) {
              return "redirect:/comments/moderation";
           } else if (authority.equals(("ROLE_USER"))) {
              return "redirect:/users/me";
           } else if (authority.equals("ROLE_HOTEL_MANAGER")) {
              return "redirect:/users/me";
           }
       }
       return "/";
   }
	
   @RequestMapping(value = "/comments/moderation")
   @AllowedForCommentModerator
   public String moderateComments(Model model) {
       model.addAttribute("hotels", hotels.findAll());
       return "comments/comment-moderating";
   }
	
   @RequestMapping(value = "/admin")
   @AllowedForAdmin
   public String manageUsers(Model model) {
       model.addAttribute("users", users.findAll());
       model.addAttribute("hotels", hotels.findAll());
       return "admin-dashboard";
   }
}