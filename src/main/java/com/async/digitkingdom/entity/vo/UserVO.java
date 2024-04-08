package com.async.digitkingdom.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateJoined;
    private LocalDate lastLogin;
    private Boolean isSuperuser;
    private Boolean isStaff;
    private Boolean isActive;
}
