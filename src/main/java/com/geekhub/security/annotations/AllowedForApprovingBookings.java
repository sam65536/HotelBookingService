package com.geekhub.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForApprovingBookings.condition)
public @interface AllowedForApprovingBookings {
    String condition = "@mySecurityService.canApproveBooking(#bookingId, principal) or " + AllowedForHotelManager.condition;
}