package com.SellerData.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, String> map=new HashMap<String, String>();
        map.put("abc","abc");
        map.put("seller","abc");
        map.put("admin","abc");
        map.put("user","abc");
        System.out.println("map >=="+map);

        String password=null;
        if (map.containsKey(username)) {
            System.out.println("get password >=="+map.get(username));
            password=map.get(username);
        }

        return new User(username,password,new ArrayList<>());
    }
}
