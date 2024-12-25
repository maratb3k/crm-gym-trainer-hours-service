package com.example.crm_gym_microservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "yearWorkloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearWorkload {
    @Id
    private String id;
    private int trainingYear;
    private String trainerWorkloadId;
    private List<String> monthWorkloadIds = new ArrayList<>();
}