package com.geekhub.controllers;

import java.time.LocalDateTime;

import com.geekhub.repositories.Comment.CommentRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import com.geekhub.repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.geekhub.domain.entities.Comment;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.domain.entities.Hotel;
import com.geekhub.domain.entities.User;
import com.geekhub.security.AllowedForApprovedComments;
import com.geekhub.security.AllowedForCommentModerator;
import com.geekhub.security.AllowedForManageComment;

@Controller
@RequestMapping(value = "/hotels")
public class CommentController {

    private final HotelRepository hotels;
    private final CommentRepository comments;
    private final UserRepository users;

    @Autowired
    public CommentController(HotelRepository hotels, CommentRepository comments, UserRepository users) {
        this.hotels = hotels;
        this.comments = comments;
        this.users = users;
    }

    @RequestMapping(value = "/{id}/comments/{commentId}/reply", method = RequestMethod.POST)
    @AllowedForApprovedComments
    public String createReply(@ModelAttribute Comment reply, @PathVariable("id") long id,
                              Model model, @PathVariable("commentId") long commentId) {
        Hotel hotel = hotels.findOne(id);
        Comment comment = comments.findOne(commentId);
        LocalDateTime date = LocalDateTime.now();
        reply.setDate(date);
        reply.setUser(getCurrentUser());
        reply.setHotel(hotel);
        reply.setIsAnswer(true);
        reply.setStatus(false);
        comments.save(reply);
        comment.setReply(reply);
        comments.save(comment);
        return "redirect:/hotels/{id}/comments";
    }

    @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
    public String createComment(@ModelAttribute Comment comment, @PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
        LocalDateTime date = LocalDateTime.now();
        comment.setDate(date);
        comment.setUser(getCurrentUser());
        comment.setHotel(hotel);
        comments.save(comment);
        return "redirect:/hotels/{id}";
    }

    @RequestMapping(value = "/{id}/comments/new", method = RequestMethod.GET)
    public String newComment(@ModelAttribute Comment comment, @PathVariable("id") long id, Model model) {
        model.addAttribute("comment", new Comment());
        model.addAttribute("hotel", hotels.findOne(id));
        model.addAttribute("users", users.findAll());
        return "comments/create";
    }

    @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
    public String showComments(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotels.findOne(id);
        Iterable<Comment> hotelComments = comments.getComments(id);
        model.addAttribute("comments", hotelComments);
        model.addAttribute("hotel", hotel);
        model.addAttribute("reply", new Comment());
        model.addAttribute("users", users.findAll());
        return "comments/hotel-comments";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/edit", method = RequestMethod.GET)
    @AllowedForManageComment
    public String editComment(@PathVariable("id") long id, @PathVariable("coomentId") long commentId, Model model) {
        Hotel hotel = hotels.findOne(id);
        model.addAttribute("hotel", hotel);
        model.addAttribute("comment", hotel.getComments().get(commentId));
        return "comments/edit";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/remove", method = RequestMethod.GET)
    @AllowedForManageComment
    public String removeComment(@PathVariable("id") long id, @PathVariable("commentId") long commentId, Model model) {
        comments.delete(commentId);
        return "redirect:/comments/moderation";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/approve", method = RequestMethod.GET)
    @AllowedForCommentModerator
    public String approveComment(@PathVariable("id") long id, @PathVariable("commentId") long commentId, Model model) {
        Comment comment = comments.findOne(commentId);
        comment.setStatus(true);
        comments.save(comment);
        return "redirect:/comments/moderation";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail myUser = (CustomUserDetail) authentication.getPrincipal();
        return myUser.getUser();
    }
}
