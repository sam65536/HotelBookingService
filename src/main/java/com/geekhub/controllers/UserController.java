package com.geekhub.controllers;

import com.geekhub.domain.CustomUserDetails;
import com.geekhub.domain.entities.Authority;
import com.geekhub.domain.entities.User;
import com.geekhub.exceptions.HotelNotFoundException;
import com.geekhub.exceptions.UserNotFoundException;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForManageUser;
import com.geekhub.security.SecurityConfig;
import com.geekhub.services.Authority.AuthorityService;
import com.geekhub.services.Booking.BookingService;
import com.geekhub.services.Comment.CommentService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.User.UserService;
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

import static com.geekhub.domain.UserRole.ROLE_USER;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final AuthorityService authorityService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final HotelService hotelService;
    private final CommentService commentService;
    private final BookingService bookingService;

    @Autowired
    public UserController(UserService userService, AuthorityService authorityService, AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService, HotelService hotelService, CommentService commentService,
                          BookingService bookingService) {

        this.userService = userService;
        this.authorityService = authorityService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.hotelService = hotelService;
        this.commentService = commentService;
        this.bookingService = bookingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForAdmin
    public String index(Model model) {
        model.addAttribute("users", userService.findAll());
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
            Authority authority = authorityService.findByRole(ROLE_USER);
            user.setAuthority(authority);
            String password = user.getPassword();
            user.setPassword(SecurityConfig.encoder.encode(user.getPassword()));
            userService.save(user);
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
        User user = userService.findOne(id);
        if (user == null) {
            throw new HotelNotFoundException();
        }
        model.addAttribute("user", user);
        model.addAttribute("hotels", hotelService.getUserHotels(id));
        model.addAttribute("comments", commentService.getUserComments(id));
        model.addAttribute("bookings", bookingService.getUserBookings(id));
        return "users/show";
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public String showActiveProfile(Model model) throws HotelNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails myUser = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.findOne(myUser.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("hotels", hotelService.getUserHotels(user.getId()));
        model.addAttribute("comments", commentService.getUserComments(user.getId()));
        model.addAttribute("bookings", bookingService.getUserBookings(user.getId()));
        return "users/show";
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForManageUser
    public String remove(@PathVariable("id") Long id, Model model) {
        User user = userService.findOne(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userService.delete(user.getId());
        model.addAttribute("users", userService.findAll());
        return "users/index";
	}

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, Model model) {
        User user = userService.findOne(id);
        model.addAttribute("user", user);
        model.addAttribute("authorities", authorityService.findAll());
        return "users/edit";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String edit(@ModelAttribute User user, Model model) {
        userService.save(user);
        model.addAttribute("user", user);
        return "redirect:/admin";
    }
}