package com.geekhub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

	@Autowired
	HotelRepository hotels;
	
	@Autowired
	UserRepository users;

	@RequestMapping(value="/")
	public String root(Model model) {
		model.addAttribute("hotels", hotels.findAll());
		return "landing-page";
	}
	
	@RequestMapping(value="/angular")
	public String angular() {		
		return "angular-interface";
	}
	
	@RequestMapping(value="/signedin")
	public String signedIn(Model model, Authentication authentication) {
		
		CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;

		if (principal != null) {
			String a = ((SimpleGrantedAuthority) principal.getAuthorities().iterator().next()).getAuthority();
			if (a.equals(("ROLE_ADMIN")))
				return "redirect:/admin";
			else if (a.equals("ROLE_COMMENT_MODERATOR"))
				return "redirect:/comments/moderation";
			else if (a.equals(("ROLE_USER")))
				return "redirect:/users/me";
			else if (a.equals("ROLE_HOTEL_MANAGER"))
				return "redirect:/users/me";
		}
		
		return "/"; // fallback
	}
	
	@RequestMapping(value="/comments/moderation")
	@AllowedForCommentModerator
	public String moderateComments(Model model)
	{
		model.addAttribute("hotels", hotels.findAll());
		return "comments/comment-moderating";
	}
	
	@RequestMapping(value="/admin")
	@AllowedForAdmin
	public String manageUsers(Model model)
	{
		model.addAttribute("users", users.findAll());
		model.addAttribute("hotels", hotels.findAll());
		return "admin-dashboard";
	}
}