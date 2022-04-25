package com.miro.miroappoauth.client.v2;

public class UserDto {
    private long id;
    private String name;

    public UserDto setId(long id) {
        this.id = id;
        return this;
    }

    public UserDto setName(String name) {
        this.name = name;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
