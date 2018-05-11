package com.events.events.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    @JsonView(Views.Summarised.class)
    private int id;

    @Column(nullable = false)
    @JsonView(Views.Summarised.class)
    private String title;

    @JsonView(Views.Summarised.class)
    private String location;

    @JsonView(Views.Summarised.class)
    private URL link;

    @JsonView(Views.Summarised.class)
    private LocalDate date;

    @JsonView(Views.EventExtended.class)
    private Currency cost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(Views.EventExtended.class)
    private User creator;

    @ManyToMany(mappedBy = "attending")
    @JsonView(Views.EventExtended.class)
    private List<User> participants;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    @JsonView(Views.EventExtended.class)
    private LocalDate createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    @JsonView(Views.EventExtended.class)
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

    public Currency getCost() {
        return cost;
    }

    public void setCost(Currency cost) {
        this.cost = cost;
    }
}
