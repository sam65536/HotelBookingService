package com.geekhub.controllers;

import com.geekhub.domain.entities.*;
import com.geekhub.exceptions.HotelNotFoundException;
import com.geekhub.exceptions.ImageNotFoundException;
import com.geekhub.security.AllowedForAdmin;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForManageHotel;
import com.geekhub.services.Category.CategoryService;
import com.geekhub.services.City.CityService;
import com.geekhub.services.Comment.CommentService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.Image.ImageService;
import com.geekhub.services.Room.RoomService;
import com.geekhub.services.RoomTypeService.RoomTypeService;
import com.geekhub.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final CommentService commentService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CategoryService categoryService;
    private final CityService cityService;
    private final ImageService imageService;

    @Autowired
    public HotelController(HotelService hotelService, RoomService roomService, RoomTypeService roomTypeService,
                           CommentService commentService, UserService userService,
                           CustomUserDetailsService customUserDetailsService, CategoryService categoryService,
                           CityService cityService, ImageService imageService) {

        this.hotelService = hotelService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.commentService = commentService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.categoryService = categoryService;
        this.cityService = cityService;
        this.imageService = imageService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("hotels", hotelService.findAll());
        return "hotels/index";
    }

    @RequestMapping(method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<Hotel> indexJSON() {
        return hotelService.findAll();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    @AllowedForHotelManager
    public String newHotel(Model model) {
        model.addAttribute("hotel", new Hotel());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("cities", cityService.findAll());
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
        List<Comment> hotelComments = commentService.getHotelComments(id);
        model.addAttribute("booking", new Booking());
        model.addAttribute("comments", hotelComments);
        model.addAttribute("hotel", hotel);
        model.addAttribute("reply", new Comment());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roomTypes", roomTypeService.findAll());
        Map<Long, Room> roomsByType = new HashMap<>();
        roomService.findAll().stream()
                .filter(room -> room.getHotel().getId() == id)
                .forEach(room -> roomsByType.putIfAbsent(room.getType().getId(), room));
        model.addAttribute("hotelRoomTypes", roomsByType.values());
        return "hotels/show";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Hotel showJSON(@PathVariable("id") long id) {
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
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("users", userService.findAll());
        return "hotels/edit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String editSave(@ModelAttribute("hotel") Hotel hotel) {
        hotel.setStatus(false);
        hotelService.save(hotel);
        return "redirect:/hotels/{id}";
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String remove(@PathVariable("id") long id) {
        hotelService.delete(id);
        return "redirect:/hotels";
    }
	
    @RequestMapping(value = "{id}/approve", method = RequestMethod.GET)
    @AllowedForAdmin
    public String approve(@PathVariable("id") long id) {
        Hotel hotel = hotelService.findOne(id);
        hotel.setStatus(true);
        hotelService.save(hotel);
        return "redirect:/admin";
    }

    @RequestMapping(value = "{id}/upload", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String uploadImage(@PathVariable("id") long id, @RequestParam("files") MultipartFile files[]) throws ImageNotFoundException {
        if (Arrays.asList(files).isEmpty()) {
            throw new ImageNotFoundException();
        }
        for (MultipartFile file : files) {
            String path = "src/main/resources/public/static/" + file.getOriginalFilename();
            try (BufferedOutputStream stream = new BufferedOutputStream
                    (new FileOutputStream(new File(path)))) {
            byte[] bytes = file.getBytes();
            stream.write(bytes);
            Image image = new Image();
            image.setHotel(hotelService.findOne(id));
            image.setInsertionDate(LocalDateTime.now());
            image.setPath(file.getOriginalFilename());
            imageService.save(image);
            } catch (Exception e) {}
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
    public String deleteImage(@PathVariable("imageId") long imageId) {
        Image image = imageService.findOne(imageId);
        imageService.delete(image.getId());
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