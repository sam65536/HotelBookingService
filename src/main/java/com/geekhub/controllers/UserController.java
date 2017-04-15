package com.geekhub.controllers;

import com.geekhub.repositories.Authority.AuthorityRepository;
import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.User.UserRepository;
import com.geekhub.services.UserService;
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

import com.geekhub.domain.entities.Authority;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.security.CustomUserDetailsService;
import com.geekhub.domain.entities.User;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForManageUser;
import com.geekhub.security.SecurityConfig;
import com.geekhub.exceptions.HotelNotFoundException;
import com.geekhub.exceptions.UserNotFoundException;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    private final UserRepository users;
    private final UserService userService;
    private final BookingRepository bookings;
    private final AuthorityRepository authorities;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(UserRepository users, UserService userService, BookingRepository bookings, AuthorityRepository authorities,
                          AuthenticationManager authMgr, CustomUserDetailsService customUserDetailsService) {
        this.users = users;
        this.userService = userService;
        this.bookings = bookings;
        this.authorities = authorities;
        this.authenticationManager = authMgr;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForAdmin
    public String index(Model model) {
        model.addAttribute("users", users.findAll());
        return "users/index";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "users/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String signUp(@ModelAttribute User user) {
        try {
            Authority authority = authorities.findByRole("ROLE_USER");
            user.setAuthority(authority);
            String password = user.getPassword();
            user.setPassword(SecurityConfig.encoder.encode(user.getPassword()));
            users.save(user);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/users/me";
        } catch (Exception e) {
            return "error";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "index";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @AllowedForManageUser
    public String show(@PathVariable("id") long id, Model model) {
        User user = users.findOne(id);
        if (user == null) {
            throw new HotelNotFoundException();
        }
        model.addAttribute("user", user);
        model.addAttribute("bookings", userService.getUserBookings(user.getId()));
        return "users/show";
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public String showActiveProfile(Model model) throws HotelNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail myUser = (CustomUserDetail) authentication.getPrincipal();
        User user = users.findOne(myUser.getUser().getId());
        model.addAttribute("bookings", userService.getUserBookings(user.getId()));
        model.addAttribute("user", user);
        return "users/show";
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForManageUser
    public String remove(@PathVariable("id") Long id, Model model) {
        User user = users.findOne(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        users.delete(user.getId());
        model.addAttribute("users", users.findAll());
        return "users/index";
	}

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, Model model) {
        User user = users.findOne(id);
        model.addAttribute("user", user);
        model.addAttribute("authorities", authorities.findAll());
        return "users/edit";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable("id") Long id, @ModelAttribute User user, Model model) {
        users.save(user);
        model.addAttribute("user", user);
        return "redirect:/admin";
    }
}