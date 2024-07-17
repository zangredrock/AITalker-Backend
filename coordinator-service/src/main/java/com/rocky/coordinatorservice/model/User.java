package com.rocky.coordinatorservice.model;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Integer id;

    private String name;

    private String password;

    private String gender;

    private String socialMediaId;

    private List<Session> sessions;

}
