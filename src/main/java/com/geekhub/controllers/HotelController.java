package com.geekhub.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.geekhub.repositories.*;
import com.geekhub.repositories.City.CityRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import com.geekhub.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.geekhub.domain.Booking;
import com.geekhub.domain.Comment;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.Image;
import com.geekhub.domain.Room;
import com.geekhub.domain.RoomType;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForManageHotel;
import com.geekhub.exceptions.HotelNotFoundException;

@Controller
@RequestMapping(value = "/hotels")
public class HotelController {

    private final HotelRepository hotels;
    private final CategoryRepository categories;
    private final RoomTypeRepository roomTypes;
    private final UserRepository users;
    private final ImageRepository images;
    private final CommentRepository comments;
    private final BookingRepository bookings;
    private final CityRepository cities;
    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelRepository hotels, CategoryRepository categories, RoomTypeRepository roomTypes,
                           UserRepository users, ImageRepository images, CommentRepository comments,
                           BookingRepository bookings, CityRepository cities, HotelService hotelService) {
        this.hotels = hotels;
        this.categories = categories;
        this.roomTypes = roomTypes;
        this.users = users;
        this.images = images;
        this.comments = comments;
        this.bookings = bookings;
        this.cities = cities;
        this.hotelService = hotelService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("hotels", hotels.findAll());
        return "hotels/index";
    }

    @RequestMapping(method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<Hotel> indexJSON(Model model) {
        return hotels.findAll();
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
        hotel.setManager(hotelService.getCurrentUser());
        hotels.save(hotel);
        model.addAttribute("hotel", hotel);
        return "redirect:/hotels";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
        if (hotel == null) {
           throw new HotelNotFoundException();
        }
        Iterable<Comment> hotelComments = comments.getComments(id);
        model.addAttribute("booking", new Booking());
        model.addAttribute("comments", hotelComments);
        model.addAttribute("hotel", hotel );
        model.addAttribute("reply", new Comment());
        model.addAttribute("users", users.findAll());
        model.addAttribute("roomTypes", roomTypes.findAll());
        Map<Long, Room> rommsMap = hotel.getRooms();
        Map<RoomType, Room> roomsTypeMap= new HashMap<>();
        for (Room room : rommsMap.values()) {
            roomsTypeMap.put(room.getType(), room);
        }
        model.addAttribute("hotelRoomTypes", roomsTypeMap);
        return "hotels/show";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Hotel showJSON(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
        if (hotel == null) {
           throw new HotelNotFoundException();
        }
        return hotel;
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String edit(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
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
        hotels.save(hotel);
        return "redirect:/hotels/{id}";
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String remove(@PathVariable("id") long id, Model model) {
        for (Room room : hotels.findOne(id).getRooms().values()) {
            for(Booking booking : room.getBookings()) {
                bookings.delete(booking);
            }
        }
        hotels.delete(id);
        return "redirect:/hotels";
    }
	
    @RequestMapping(value = "{id}/approve", method = RequestMethod.GET)
    @AllowedForAdmin
    public String approve(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
        hotel.setStatus(true);
        hotels.save(hotel);
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
                   image.setHotel(hotels.findOne(id));
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
        Hotel hotel = hotels.findOne(id);
        model.addAttribute("hotel", hotel);
        return "hotels/upload";
    }
	
    @RequestMapping(value="{id}/remove_image/{imageId}", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String deleteImage(@PathVariable("id") long id, @PathVariable("imageId") long imageId, Model model) {
        Image image = images.findOne(imageId);
        images.delete(image);
        return "redirect:/hotels/{id}/upload";
    }

    @RequestMapping(value = "{id}/map", method = RequestMethod.POST)
    @AllowedForHotelManager
    public String hotelMap(@PathVariable("id") long id, Model model, @ModelAttribute Booking booking) {
        model.addAttribute("beginDate", booking.getBeginDate());
        model.addAttribute("endDate", booking.getEndDate());
        model.addAttribute("hotel", hotels.findOne(id));
        model.addAttribute("occupancy", hotelService.getOccupancy(hotels.findOne(id), booking.getBeginDate(), booking.getEndDate()));
        return "hotels/map";
    }
}