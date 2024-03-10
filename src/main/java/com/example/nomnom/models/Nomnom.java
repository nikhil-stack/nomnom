package com.example.nomnom.models;

import jakarta.persistence.*;
import lombok.*;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table
@Data
public class Nomnom {

    @Id
    @SequenceGenerator(
            name = "nomnom_sequence",
            sequenceName = "nomnom_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "nomnom_sequence"
    )
    private Long id;

    @Column(updatable = false)
    private String name;

    private String icon;
    private String description;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
