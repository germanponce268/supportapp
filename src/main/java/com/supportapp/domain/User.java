package com.supportapp.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
    @Getter
    @Setter
    @Entity
    @Table( schema = "users")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String userId;
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String email;
        private String profileImageUrl;
        private Date lastLoginDate;
        private Date lastLogindDateDisplay;
        private Date joinDate;
        private String role; //ROLE_USER, ROLE_ADMIN
        private String[] authorities;
        private boolean isActive;
        private boolean isNotLocked;

        public User(){};
        public User(Long id, String userId, String firstName, String lastName, String username, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLogindDateDisplay, Date joinDate, String role, String[] authorities, boolean isActive, boolean isNotLocked) {
            this.id = id;
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.email = email;
            this.profileImageUrl = profileImageUrl;
            this.lastLoginDate = lastLoginDate;
            this.lastLogindDateDisplay = lastLogindDateDisplay;
            this.joinDate = joinDate;
            this.role = role;
            this.authorities = authorities;
            this.isActive = isActive;
            this.isNotLocked = isNotLocked;
        }
}
