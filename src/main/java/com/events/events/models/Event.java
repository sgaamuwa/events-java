package com.events.events.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.RepresentationModel;;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@Table(name = "events")
public class Event extends RepresentationModel<Event> {

    @Id
    @GeneratedValue
    private int eventId;

    @Column(nullable = false)
    @NotEmpty
    private String title;

    private String location;

    private URL link;

    private LocalDate date;

    private Currency cost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"createdEvents", "attending", "createdAt", "updatedAt"})
    private User creator;

    @ManyToMany(mappedBy = "attending")
    private Set<User> participants;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDate updatedAt;

    private EventStatus eventStatus = EventStatus.OPEN;

    public Event(){
        this.participants = new HashSet<>();
    }

    public Event(String title, String location, LocalDate date, User creator) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.creator = creator;
        this.participants = new HashSet<>();
    }

    public Event(String title, String location, URL link, LocalDate date, User creator) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.date = date;
        this.creator = creator;
        this.participants = new HashSet<>();
    }

    public Event(String title, String location, URL link, LocalDate date, User creator, Set<User> participants) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.date = date;
        this.creator = creator;
        this.participants = participants;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
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

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Event)){
            return false;
        }

        return Integer.compare(eventId, ((Event) obj).eventId) == 0;
    }
}
