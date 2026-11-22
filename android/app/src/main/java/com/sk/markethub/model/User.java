package com.sk.markethub.model;

import java.util.Set;

public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private Set<String> roles;

    public User() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMobile() { return mobile; }
    public Set<String> getRoles() { return roles; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
