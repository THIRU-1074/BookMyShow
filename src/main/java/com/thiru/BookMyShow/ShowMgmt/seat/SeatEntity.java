package com.thiru.BookMyShow.ShowMgmt.seat;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
                @UniqueConstraint(name = "uk_seat_auditorium_seatno", columnNames = { "auditorium_id", "seat_no" })
}, indexes = {

                // fast full seating load
                @Index(name = "idx_seat_auditorium_id", columnList = "auditorium_id"),

                // fast ordered seating layout
                @Index(name = "idx_seat_auditorium_layout", columnList = "auditorium_id, row_no, col_no")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long seatId;

        @Column(name = "seatNo")
        private String seatNo; // A1, A2, B3 etc

        @Column(name = "rowNo")
        private Integer row;

        @Column(name = "colNo")
        private Integer col;

        @Column(name = "stanceNo")
        private Integer stance;

        @ManyToOne
        @JoinColumn(name = "auditoriumId")
        private AuditoriumEntity auditorium;

}
