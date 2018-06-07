package com.events.events.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @JsonView(Views.Summarised.class)
    private int id;

    @JsonView(Views.Summarised.class)
    private String firstName;

    @JsonView(Views.Summarised.class)
    private String lastName;

    @JsonView(Views.Summarised.class)
    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 5, message = "Password must be {min} or more characters long")
    private String password;

    @Email
    @JsonView(Views.Summarised.class)
    private String email;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    @JsonView(Views.UserExtended.class)
    private LocalDate createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    @JsonView(Views.UserExtended.class)
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "creator")
    @JsonView(Views.UserExtended.class)
    private List<Event> createdEvents;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_event",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
    @JsonView(Views.UserExtended.class)
    private List<Event> attending;

    public User(){
        this.createdEvents = Collections.emptyList();
        this.attending = Collections.emptyList();
    }

    public User(String username, String password){
        this();
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email){
        this(username, password);
        this.email = email;
    }

    public User(String firstName, String lastName, String username, String password, String email) {
        this(username, password, email);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(List<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public List<Event> getAttending() {
        return attending;
    }

    public void setAttending(List<Event> attending) {
        this.attending = attending;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public LocalDate getUpdatedAt(){
        return updatedAt;
    }
}
