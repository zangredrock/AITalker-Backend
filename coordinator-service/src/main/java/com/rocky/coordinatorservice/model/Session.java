package com.rocky.coordinatorservice.model;

import lombok.Data;

@Data
public class Session {

    private Integer id;

    private String name;

    private String remark;

    private Integer userId;

    private User user;

}
