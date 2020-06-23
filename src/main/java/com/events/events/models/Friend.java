package com.events.events.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "friends")
public class Friend {

    @EmbeddedId
    private Key key;

    private boolean isActive;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDate updatedAt;

    public Friend(){};

    public Friend(@NotNull User owner, @NotNull User friend){
        this.isActive = false;
        this.key = new Key(owner, friend);
    }

    public User getOwner() {
        return key.owner;
    }

    public User getFriend() {
        return key.friend;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public LocalDate getUpdatedAt(){
        return updatedAt;
    }

    @Embeddable
    public static class Key implements Serializable {

        @ManyToOne
        private User owner;

        @ManyToOne
        private User friend;

        public Key(){}
        public Key(User owner, User friend){
            this();
            this.owner = owner;
            this.friend = friend;
        }

        public User getOwner() {
            return owner;
        }

        public void setOwnerId(User owner) {
            this.owner = owner;
        }

        public User getFriend() {
            return friend;
        }

        public void setFriendId(User friend) {
            this.friend = friend;
        }

        @Override
        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj == null){
                return false;
            }
            if(!(obj instanceof Friend.Key)){
                return false;
            }
            return this.owner.equals(((Key) obj).owner) && this.friend.equals(((Key) obj).friend);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.owner, this.friend);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Friend)){
            return false;
        }
        return key.owner.equals(((Friend) obj).key.owner) && key.friend.equals(((Friend) obj).key.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key.owner, key.friend);
    }
}
