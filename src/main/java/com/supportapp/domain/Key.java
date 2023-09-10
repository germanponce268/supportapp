package com.supportapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Key {
    @Id
    private Long id;
    private String password;

}
