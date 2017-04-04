package com.geekhub.security;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForApprovedComments.condition)
public @interface AllowedForApprovedComments {
    String condition = "@mySecurityService.canReplyToComment(#id, #commentId, principal)";
}
