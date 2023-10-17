package com.example.toDoList.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.toDoList.user.userRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class filterUserAuth extends OncePerRequestFilter {

    @Autowired
    private userRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if(servletPath.equals("/users/update") || servletPath.equals("/users/delete")){

            var aut = request.getHeader("Authorization");

            if(aut == null){
                response.sendError(401,"Nenhum login informado.");
            }

            var user_password = aut.substring("Basic".length()).trim();

            byte[] autDecode = Base64.getDecoder().decode(user_password);

            String autString = new String(autDecode);

            String[] credentials = autString.split(":");

            String user = credentials[0];
            String password = credentials[1];

            var validUser = this.repository.findByUsername(user);
            //valida usuário
            if (validUser == null){
                response.sendError(401,"Usuário sem autorização");
            }else {
                //valida senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), validUser.getPassword());
                if (passwordVerify.verified){
                    //segue
                    request.setAttribute("userId",validUser.getId());

                    filterChain.doFilter(request,response);

                }else {
                    response.sendError(401,"Usuário sem autorização");
                }

            }

        } else {
            filterChain.doFilter(request,response);
        }
    }

}


