package library_management.com.Entity;

import jakarta.persistence.*;
import library_management.com.Model.PinTuru;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "pinler")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pinler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude" , nullable = false)
    private double latitude;

    @Column(name = "longitude" , nullable = false)
    private double longitude;

    @Column(name = "pin_turu" , nullable = false)
    @Enumerated(EnumType.STRING)
    private PinTuru pinTuru;

    @Column(name = "aktif" , nullable = false)
    @ColumnDefault("true")
    private boolean aktif = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
