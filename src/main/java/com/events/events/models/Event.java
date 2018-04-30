package com.events.events.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;
    private String location;

    @org.hibernate.validator.constraints.URL
    private URL link;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToMany(mappedBy = "attending")
    private List<User> participants;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDate updatedAt;

    public Event(){
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, LocalDate date, User creator) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.creator = creator;
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, URL link, LocalDate date, User creator) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.date = date;
        this.creator = creator;
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, URL link, LocalDate date, User creator, List<User> participants) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.date = date;
        this.creator = creator;
        this.participants = participants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public LocalDate getDate() {
        return date;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public LocalDate getUpdatedAt(){
        return updatedAt;
    }
}
