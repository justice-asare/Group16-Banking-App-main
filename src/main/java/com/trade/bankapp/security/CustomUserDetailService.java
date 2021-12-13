package com.trade.bankapp.security;

import com.trade.bankapp.users.Client;
import com.trade.bankapp.users.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private final ClientRepo clientRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientRepo.findByEmail(username);
        if (client == null) {
            throw new UsernameNotFoundException("Client not found");
        }
        return new CustomUserDetail(client);
    }
}
