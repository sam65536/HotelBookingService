package com.geekhub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.geekhub.domain.Booking;
import com.geekhub.domain.Comment;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.User;
import com.geekhub.repositories.BookingRepository;
import com.geekhub.repositories.CommentRepository;
import com.geekhub.repositories.HotelRepository;
import com.geekhub.repositories.UserRepository;

@Component("mySecurityService")
public class MySecurityService {

   private HotelRepository hotels;
   private UserRepository users;
   private CommentRepository comments;
   private BookingRepository bookings;

   @Autowired
   public void setHotels(HotelRepository hotels) {
       this.hotels = hotels;
   }

   @Autowired
   public void setUsers(UserRepository users) {
       this.users = users;
   }

   @Autowired
   public void setComments(CommentRepository comments) {
       this.comments = comments;
   }

   @Autowired
   public void setBookings(BookingRepository bookings) {
       this.bookings = bookings;
   }

   public boolean canEditHotel(long hotelId, CustomUserDetail user) {
       Hotel hotel = hotels.findOne(hotelId);
       return (hotel != null) && (hotel.getManager() != null)
            && ( user.getUser().getId() == hotel.getManager().getId() );
   }

   public boolean canEditHotel(long hotelId, String s) {
       return false;
   }

   public boolean canEditUser(long userId, CustomUserDetail user) {
       User userTmp = users.findOne(userId);
       return (userTmp != null) && (user.getUser() != null)
            && ( user.getUser().getId() == userTmp.getId() );
   }

   public boolean canEditComment(long commentId, CustomUserDetail user) {
       Comment comment = comments.findOne(commentId);
       return (comment != null) && (user != null)
            && ( comment.getUser().getId() == user.getUser().getId() );
   }

   public boolean canReplyToComment(long id, long commentId, CustomUserDetail user) {
       Hotel hotel = hotels.findOne(id);
       Comment comment = comments.findOne(commentId);
       return (comment != null) && (user != null)
            && (comment.getStatus()) && (!comment.getIsAnswer()) && (hotel != null)
            && ( hotel.getManager().getId() == user.getUser().getId() );
   }

   public boolean canApproveBooking(long bookingId, CustomUserDetail user) {
       Booking booking = bookings.findOne(bookingId);
       return (booking != null) && (user != null)
            && ( booking.getHotel().getManager().getId() == user.getUser().getId() );
   }

   public boolean canRemoveBooking(long bookingId, CustomUserDetail user) {
       Booking booking = bookings.findOne(bookingId);
       return (booking != null) && (user != null)
            && (
                    ( booking.getHotel().getManager().getId() == user.getUser().getId() )
                 || ( booking.getUser().getId() == user.getUser().getId() )
            );
   }
}