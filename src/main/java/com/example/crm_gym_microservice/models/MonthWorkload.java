package com.example.crm_gym_microservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "monthWorkloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthWorkload {
    @Id
    private String id;
    private int trainingMonth;
    private double trainingSummaryDuration;
    private String yearWorkloadId;
}
