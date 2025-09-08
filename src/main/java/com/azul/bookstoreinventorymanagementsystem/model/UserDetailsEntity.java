package com.azul.bookstoreinventorymanagementsystem.model;

import com.azul.bookstoreinventorymanagementsystem.model.enums.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "user_details")
public class UserDetailsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequence")
  @SequenceGenerator(name = "userSequence", sequenceName = "user_id_seq", allocationSize = 1)
  private Long id;

  private String username;

  private String password;

  @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Set<UserRole> roles;
}
