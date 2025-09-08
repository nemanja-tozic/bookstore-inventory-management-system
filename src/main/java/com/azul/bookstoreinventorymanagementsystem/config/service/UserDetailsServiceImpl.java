package com.azul.bookstoreinventorymanagementsystem.config.service;

import com.azul.bookstoreinventorymanagementsystem.model.enums.UserRole;
import com.azul.bookstoreinventorymanagementsystem.repository.UserDetailsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserDetailsJpaRepository userDetailsJpaRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final var userDetailsEntity = userDetailsJpaRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    return User.builder()
        .username(username)
        .password(userDetailsEntity.getPassword())
        .roles(userDetailsEntity.getRoles().stream().map(UserRole::name).toArray(String[]::new))
        .build();
  }
}
