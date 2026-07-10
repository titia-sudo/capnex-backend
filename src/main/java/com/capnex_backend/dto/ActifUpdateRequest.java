package com.capnex_backend.dto;

import lombok.Data;

@Data
public class ActifUpdateRequest {
    private Double prix;
    private Integer liq;
    private Integer ca;
    private Integer croiss;
    private Double per5;
    private Integer note;
    private Integer marge;
    private Double freq;
    private Double notediv;
    private String sigM;
    private String sigW;
    private String sigD;
    private Integer dailyM;
    private Double entree;
    private Double sl;
    private Double tp;
}