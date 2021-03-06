package com.example.image.repo.filters;

import com.example.image.repo.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends GenericFilterBean {

    /* FILTER OUT REQUESTS IF UNAUTHORIZED */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader!=null){
            String[] authHeaderArr = authHeader.split("Bearer");
            if (authHeaderArr.length>1 && authHeaderArr[1]!=null){
                String token = authHeaderArr[1];
                try{
                    Claims claims = Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
                            .parseClaimsJws(token).getBody();
                    httpRequest.setAttribute("email", claims.get("email"));
                } catch (Exception e){
                    httpResponse.sendError(HttpStatus.SC_FORBIDDEN,"invalid token");
                    return;
                }
            } else {
                httpResponse.sendError(HttpStatus.SC_FORBIDDEN,"invalid token");
                return;
            }
        } else {
            httpResponse.sendError(HttpStatus.SC_FORBIDDEN,"invalid token");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
