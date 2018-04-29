package com.events.events.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
    private URL link;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToMany(mappedBy = "attending")
    private List<User> participants;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Date updatedAt;

    public Event(){
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, Date date, User creator) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.creator = creator;
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, URL link, Date date, User creator) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.date = date;
        this.creator = creator;
        this.participants = new ArrayList<>();
    }

    public Event(String title, String location, URL link, Date date, User creator, List<User> participants) {
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

    public Date getDate() {
        return date;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public Date getUpdatedAt(){
        return updatedAt;
    }
}
