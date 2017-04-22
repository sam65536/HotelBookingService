package com.geekhub.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForAdmin.condition)
public @interface AllowedForAdmin {
    String condition = "hasRole(T(com.geekhub.domain.UserRole).ROLE_ADMIN.toString())";
}
