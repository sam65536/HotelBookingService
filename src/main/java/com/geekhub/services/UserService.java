package com.geekhub.services;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Comment;
import com.geekhub.domain.entities.Hotel;
import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Comment.CommentRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final BookingRepository bookings;
    private final CommentRepository comments;
    private final HotelRepository hotels;

    @Autowired
    public UserService(BookingRepository bookings, CommentRepository comments, HotelRepository hotels) {
        this.bookings = bookings;
        this.comments = comments;
        this.hotels = hotels;
    }

    public List<Booking> getUserBookings(long userId) {
        List<Booking> userBookings = bookings.getUserBookings(userId);
        return userBookings;
    }

    public List<Comment> getUserComments(long userId) {
        List<Comment> userComments = comments.getCommentsOfUser(userId);
        return userComments;
    }

    public List<Hotel> getUserHotels(long userId) {
        List<Hotel> userHotels = hotels.getUserHotels(userId);
        return userHotels;
    }
}