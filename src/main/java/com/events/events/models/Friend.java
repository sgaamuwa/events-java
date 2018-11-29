package com.events.events.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Friend {

    @EmbeddedId
    private Key key;

    @ManyToOne
    @MapsId("ownerId")
    private User owner;

    @ManyToOne
    @MapsId("friendId")
    private User friend;

    private boolean isActive;

    public Friend(@NotNull User owner, @NotNull User friend){
        this.owner = owner;
        this.friend = friend;
    }

    public User getOwner() {
        return owner;
    }

    public User getFriend() {
        return friend;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Embeddable
    public static class Key implements Serializable {
        private int ownerId;
        private int friendId;
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
        return owner.equals(((Friend) obj).owner) && friend.equals(((Friend) obj).friend);
    }
}
