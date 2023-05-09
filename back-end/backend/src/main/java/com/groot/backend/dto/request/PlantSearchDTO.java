package com.groot.backend.dto.request;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantSearchDTO {

    private String name;

    private String[] difficulty;

    private String[] lux;

    private String[] growth;

    private int page;
}
