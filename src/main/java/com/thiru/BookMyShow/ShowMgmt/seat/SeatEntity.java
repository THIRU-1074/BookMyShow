package com.thiru.BookMyShow.ShowMgmt.seat;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = { "auditorium_id", "seat_no" }))
@Getter
@Setter
@AllArgsConstructor
@Builder
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(name = "seatNo")
    private String seatNo; // A1, A2, B3 etc

    @Column(name = "rowNo")
    private final Integer row;

    @Column(name = "colNo")
    private final Integer col;

    @Column(name = "stanceNo")
    private final Integer stance;

    @ManyToOne
    @JoinColumn(name = "auditoriumId")
    private AuditoriumEntity auditorium;

}
