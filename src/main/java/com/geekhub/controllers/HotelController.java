package com.geekhub.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Category.CategoryRepository;
import com.geekhub.repositories.City.CityRepository;
import com.geekhub.repositories.Comment.CommentRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import com.geekhub.repositories.Image.ImageRepository;
import com.geekhub.repositories.Room.RoomRepository;
import com.geekhub.repositories.RoomType.RoomTypeRepository;
import com.geekhub.repositories.User.UserRepository;
import com.geekhub.services.Booking.BookingService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.Room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Comment;
import com.geekhub.domain.entities.Hotel;
import com.geekhub.domain.entities.Image;
import com.geekhub.domain.entities.Room;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForManageHotel;
import com.geekhub.exceptions.HotelNotFoundException;

@Controller
@RequestMapping(value = "/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final CategoryRepository categories;
    private final RoomTypeRepository roomTypes;
    private final UserRepository users;
    private final ImageRepository images;
    private final CommentRepository comments;
    private final CityRepository cities;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public HotelController(HotelService hotelService, RoomService roomService, BookingService bookingService,
                           CategoryRepository categories, RoomTypeRepository roomTypes, UserRepository users,
                           ImageRepository images, CommentRepository comments, CityRepository cities,
                           CustomUserDetailsService customUserDetailsService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.categories = categories;
        this.roomTypes = roomTypes;
        this.users = users;
        this.images = images;
        this.comments = comments;
        this.cities = cities;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("hotels", hotelService.findAll());
        return "hotels/index";
    }

    @RequestMapping(method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<Hotel> indexJSON(Model model) {
        return hotelService.findAll();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    @AllowedForHotelManager
    public String newHotel(Model model) {
        model.addAttribute("hotel", new Hotel());
        model.addAttribute("categories", categories.findAll());
        model.addAttribute("cities", cities.findAll());
        return "hotels/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    @AllowedForHotelManager
    public String saveIt(@ModelAttribute Hotel hotel, Model model) {
        hotel.setManager(customUserDetailsService.getCurrentUser());
        hotelService.save(hotel);
        model.addAttribute("hotel", hotel);
        return "redirect:/hotels";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        if (hotel == null) {
           throw new HotelNotFoundException();
        }
        hotel.setRooms(roomService.getHotelRooms(id));

        List<Comment> hotelComments = comments.getCommentsOfHotel(id);
        model.addAttribute("booking", new Booking());
        model.addAttribute("comments", hotelComments);
        model.addAttribute("hotel", hotel);
        model.addAttribute("reply", new Comment());
        model.addAttribute("users", users.findAll());
        model.addAttribute("roomTypes", roomTypes.findAll());
        Map<Long, Room> roomsByType = new HashMap<>();
                roomService.findAll().stream()
                .filter(room -> room.getHotel().getId() == id)
                .forEach(room -> roomsByType.putIfAbsent(room.getType().getId(), room));
        model.addAttribute("hotelRoomTypes", roomsByType.values());
        return "hotels/show";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Hotel showJSON(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        if (hotel == null) {
           throw new HotelNotFoundException();
        }
        return hotel;
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String edit(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        model.addAttribute("hotel", hotel);
        model.addAttribute("categories", categories.findAll());
        model.addAttribute("cities", cities.findAll());
        model.addAttribute("users", users.findAll());
        return "hotels/edit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String editSave(@PathVariable("id") long id, @ModelAttribute("hotel") Hotel hotel) {
        hotel.setStatus(false);
        hotelService.save(hotel);
        return "redirect:/hotels/{id}";
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String remove(@PathVariable("id") long id, Model model) {
        hotelService.delete(id);
        return "redirect:/hotels";
    }
	
    @RequestMapping(value = "{id}/approve", method = RequestMethod.GET)
    @AllowedForAdmin
    public String approve(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        hotel.setStatus(true);
        hotelService.save(hotel);
        return "redirect:/admin";
    }

    @RequestMapping(value = "{id}/upload", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String uploadImage(@PathVariable("id") long id, Model model, @RequestParam("files") MultipartFile files[]) {
        if (files.length > 0) {
           for (int i = 0; i < files.length; i++) {
               MultipartFile file = files[i];
               try {
                   byte[] bytes = file.getBytes();
                   String path = "src/main/resources/public/static/" + file.getOriginalFilename();
                   BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path)));
                   stream.write(bytes);
                   stream.close();
                   Image image = new Image();
                   image.setHotel(hotelService.findOne(id));
                   image.setInsertionDate(LocalDateTime.now());
                   image.setPath(file.getOriginalFilename());
                   images.save(image);
               } catch (Exception e) {}
           }
        }
        return "redirect:/users/me";
    }
	
    @RequestMapping(value = "{id}/upload", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String uploadForm(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        model.addAttribute("hotel", hotel);
        return "hotels/upload";
    }
	
    @RequestMapping(value="{id}/remove_image/{imageId}", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String deleteImage(@PathVariable("id") long id, @PathVariable("imageId") long imageId, Model model) {
        Image image = images.findOne(imageId);
        images.delete(image.getId());
        return "redirect:/hotels/{id}/upload";
    }

    @RequestMapping(value = "{id}/map", method = RequestMethod.POST)
    @AllowedForHotelManager
    public String hotelMap(@PathVariable("id") long id, Model model,
                           @RequestParam("beginDate")
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
                           @RequestParam("endDate")
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        model.addAttribute("beginDate", beginDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("hotel", hotelService.findOne(id));
        model.addAttribute("occupancy", hotelService.getOccupancy(hotelService.findOne(id), beginDate, endDate));
        return "hotels/map";
    }
}