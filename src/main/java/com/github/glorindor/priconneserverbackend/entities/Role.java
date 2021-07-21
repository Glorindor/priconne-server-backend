package com.github.glorindor.priconneserverbackend.entities;

public enum Role {
    CLAN_LEADER,
    VICE_LEADER,
    MEMBER;

    public String getRole() {
        return this.name();
    }
}
