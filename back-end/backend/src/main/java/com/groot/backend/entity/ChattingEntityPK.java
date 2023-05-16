package com.groot.backend.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ChattingEntityPK implements Serializable {
    private Long sender;
    private Long receiver;
}
