package com.supportportal.suppourtportal.filter;

//Handles any AccessDeniedException and AuthenticationExceptionthrown within the filter chain. 
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import static com.supportportal.suppourtportal.constant.SecurityConstant.*;
import com.supportportal.suppourtportal.constant.SecurityConstant.*;
import java.io.IOException;
import java.io.OutputStream;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportportal.suppourtportal.domain.HttpResponse;
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(), ACESS_DENIED_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}

