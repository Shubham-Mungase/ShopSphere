package com.shopsphere.order.domain.utils;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopsphere.order.domain.filter.CustomAuthUserPrinciple;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        return UUID.fromString(
        	    ((CustomAuthUserPrinciple) SecurityContextHolder
        	            .getContext()
        	            .getAuthentication()
        	            .getPrincipal())
        	            .getUserId()
        	    );
    }
}
