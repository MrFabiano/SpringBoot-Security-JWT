package com.microservices.apis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Consultar no banco o usuario

        Usuario usuario = usuarioRepository.findUserByLogin(username);
        if(usuario == null){
             throw new UsernameNotFoundException("Usuario não encontrado");
        } 
        return new User(usuario.getLogin(),  
                     usuario.getPassword(), 
                  usuario.getAuthorities());
     }
}
