package io.github.eetchyza.springauth.web;

import io.github.eetchyza.springauth.AuthService;
import io.github.eetchyza.springauth.SecurityContext;
import io.github.eetchyza.springauth.annotations.AllowAnon;
import io.github.eetchyza.springauth.api.UserDetails;
import io.github.eetchyza.springauth.exceptions.NotAuthenticatedException;
import io.github.eetchyza.springauth.exceptions.NotAuthorisedException;
import io.github.eetchyza.springauth.exceptions.PasswordExpiredException;
import io.github.eetchyza.springauth.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class SecurityFilter implements HandlerInterceptor {
    @Autowired
    private AuthService authService;
    private Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if(handler.getClass().isAssignableFrom(ResourceHttpRequestHandler.class)){
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();

        String token = request.getHeader("TOKEN");
        try {
            authService.setCurrentUser(token);
            UserDetails user = SecurityContext.getCurrentUser();

            if (user != null) {
                logger.info("{} request made to '{}' by user: {}", request.getMethod(), request.getPathInfo(), user.getUsername());
            } else {
                logger.info("{} request made to '{}' by anonymous user", request.getMethod(), request.getPathInfo());
            }

            if (!method.isAnnotationPresent(AllowAnon.class)) {
                authService.checkAuthenticated(token);
            }

            authService.checkIsAuthorised(token, method);
        } catch (NotAuthorisedException | NotAuthenticatedException | TokenExpiredException | PasswordExpiredException e) {
            UserDetails user = SecurityContext.getCurrentUser();
            String username;

            if (user != null){
                username = user.getUsername();
            }else{
                username = "anonymous user";
            }

            logger.warn("[{}]: {}", username, e.getMessage());
            response.setContentType("application/json");
            response.getWriter().print("{\"message\":\"" + e.getMessage() + "\"}");
            response.sendError(403, e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        SecurityContext.clear();
    }
}
