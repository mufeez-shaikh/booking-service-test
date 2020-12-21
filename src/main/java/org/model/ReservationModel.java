package org.model;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationModel {

    private Long id;

    private long campSiteId;

    private String startDate;

    private String endDate;

    private String email;

    private String bookedBy;

    private String status;
}

