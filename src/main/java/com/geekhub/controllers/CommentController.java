package com.geekhub.controllers;

import com.geekhub.domain.entities.Comment;
import com.geekhub.domain.entities.Hotel;
import com.geekhub.security.annotations.AllowedForApprovedComments;
import com.geekhub.security.annotations.AllowedForCommentModerator;
import com.geekhub.security.annotations.AllowedForManageComment;
import com.geekhub.services.Comment.CommentService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Controller
@RequestMapping(value = "/hotels")
public class CommentController {

    private final HotelService hotelService;
    private final CommentService commentService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public CommentController(HotelService hotelService, CommentService commentService, UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.hotelService = hotelService;
        this.commentService = commentService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(value = "/{id}/comments/{commentId}/reply", method = RequestMethod.POST)
    @AllowedForApprovedComments
    public String createReply(@ModelAttribute Comment reply, @PathVariable("id") long id,
                              @PathVariable("commentId") long commentId) {
        Hotel hotel = hotelService.findOne(id);
        Comment comment = commentService.findOne(commentId);
        LocalDateTime date = LocalDateTime.now();
        reply.setDate(date);
        reply.setUser(customUserDetailsService.getCurrentUser());
        reply.setHotel(hotel);
        reply.setIsAnswer(true);
        reply.setStatus(false);
        commentService.save(reply);
        comment.setReply(reply);
        commentService.save(comment);
        return "redirect:/hotels/{id}/comments";
    }

    @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
    public String createComment(@ModelAttribute Comment comment, @PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        LocalDateTime date = LocalDateTime.now();
        comment.setDate(date);
        comment.setUser(customUserDetailsService.getCurrentUser());
        comment.setHotel(hotel);
        commentService.save(comment);
        return "redirect:/hotels/{id}";
    }

    @RequestMapping(value = "/{id}/comments/new", method = RequestMethod.GET)
    public String newComment(@ModelAttribute Comment comment, @PathVariable("id") long id, Model model) {
        model.addAttribute("comment", new Comment());
        model.addAttribute("hotel", hotelService.findOne(id));
        model.addAttribute("users", userService.findAll());
        return "comments/create";
    }

    @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
    public String showComments(@PathVariable("id") long id, Model model) {
        Hotel hotel = hotelService.findOne(id);
        Iterable<Comment> hotelComments = commentService.getHotelComments(id);
        model.addAttribute("comments", hotelComments);
        model.addAttribute("hotel", hotel);
        model.addAttribute("reply", new Comment());
        model.addAttribute("users", userService.findAll());
        return "comments/hotel-comments";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/edit", method = RequestMethod.GET)
    @AllowedForManageComment
    public String editComment(@PathVariable("id") long id, @PathVariable("commentId") long commentId, Model model) {
        Hotel hotel = hotelService.findOne(id);
        model.addAttribute("hotel", hotel);
        model.addAttribute("comment", hotel.getComments().get(commentId));
        return "comments/edit";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/remove", method = RequestMethod.GET)
    @AllowedForManageComment
    public String removeComment(@PathVariable("commentId") long commentId) {
        commentService.delete(commentId);
        return "redirect:/comments/moderation";
    }

    @RequestMapping(value = "{id}/comments/{commentId}/approve", method = RequestMethod.GET)
    @AllowedForCommentModerator
    public String approveComment(@PathVariable("commentId") long commentId) {
        Comment comment = commentService.findOne(commentId);
        comment.setStatus(true);
        commentService.save(comment);
        return "redirect:/comments/moderation";
    }
}