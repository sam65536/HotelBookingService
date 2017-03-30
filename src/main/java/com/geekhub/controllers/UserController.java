package com.geekhub.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.geekhub.domain.Authority;
import com.geekhub.domain.Booking;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.service.CustomUserDetailsService;
import com.geekhub.domain.User;
import com.geekhub.repositories.AuthorityRepository;
import com.geekhub.repositories.BookingRepository;
import com.geekhub.repositories.UserRepository;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForManageUser;
import com.geekhub.security.SecurityConfig;
import com.geekhub.exceptions.HotelNotFoundException;
import com.geekhub.exceptions.UserNotFoundException;

@Controller
@RequestMapping(value="/users")
public class UserController {

	@Autowired
	UserRepository users;

	@Autowired
	BookingRepository bookings;

	@Autowired
	AuthorityRepository authorities;

	@Autowired 
	private AuthenticationManager authMgr;

	@Autowired 
	private CustomUserDetailsService customUserDetailsSvc;

	@RequestMapping(method=RequestMethod.GET)
	@AllowedForAdmin
	public String index(Model model) {
		model.addAttribute("users", users.findAll());
		return "users/index";
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public String newHotel(Model model) {
		model.addAttribute("user", new User());
		return "users/create";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String saveIt(@ModelAttribute User user, Model model)
	{
		try {
			Authority authority = authorities.findByRole("ROLE_USER");
			user.setAuthority(authority);
			String pass = user.getPassword();
			user.setPassword(SecurityConfig.encoder.encode(user.getPassword()));
			users.save(user);
			UserDetails userDetails = customUserDetailsSvc.loadUserByUsername(user.getUsername());
			
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, pass, userDetails.getAuthorities());
			authMgr.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(auth);
			return "redirect:/users/me";
		}
		catch(Exception e){
			return "error";
		}
	}

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(Model model) {
		return "index";
	}

	@RequestMapping(value="{id}", method=RequestMethod.GET) 
	@AllowedForManageUser
	public String show(@PathVariable("id") long id, Model model) {
		User user = users.findOne(id);
		if( user == null )
			throw new HotelNotFoundException();    	
		model.addAttribute("user", user);    
		model.addAttribute("bookings", getUserBookings(user.getId()));   
		return "users/show";
	}

	@RequestMapping(value="/me", method=RequestMethod.GET)
	public String showActiveProfile(Model model) throws HotelNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	    
		CustomUserDetail myUser= (CustomUserDetail) auth.getPrincipal();
		User user = users.findOne(myUser.getUser().getId());
		model.addAttribute("bookings", getUserBookings(user.getId()));    	    	
		model.addAttribute("user", user);
		return "users/show";
	}

	public Iterable<Booking> getUserBookings(long user_id) {
		Iterator<Booking> itbookings = bookings.findAll().iterator();
		List<Booking> bookingsList = new ArrayList<Booking>();

		while(itbookings.hasNext()) {
			Booking b = itbookings.next();
			if(b.getUser().getId() == user_id)
				bookingsList.add(b);
		}
		return bookingsList;
	}    

	@RequestMapping(value="{id}/remove", method=RequestMethod.GET)
	@AllowedForManageUser
	public String remove(@PathVariable("id") long id, Model model) {
		User user = users.findOne(id);    	
		if( user == null )
			throw new UserNotFoundException();    	
		users.delete(user);
		model.addAttribute("users", users.findAll());
		return "users/index";
	}  

	@RequestMapping(value="{id}/edit", method=RequestMethod.GET)
	public String edit(@PathVariable("id") long id, Model model) { 	
		User user = users.findOne(id);
		model.addAttribute("user", user);    	
		model.addAttribute("authorities", authorities.findAll());
		return "users/edit";
	}

	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String edit(@PathVariable("id") long id, @ModelAttribute User user, Model model) {
		users.save(user);
		model.addAttribute("user", user);
		return "redirect:/admin";
	}	
}