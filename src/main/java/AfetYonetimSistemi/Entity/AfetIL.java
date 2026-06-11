package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "afet_iller")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AfetIL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "afet_id")
    private Afet afet;

    @ManyToOne
    @JoinColumn(name = "il_id")
    private IL il;

    @Column(name = "aktif" , nullable = false)
    private boolean aktif;

    @Column(name = "baslangic_tarihi" , nullable = false)
    private LocalDateTime baslangicTarihi;

    @Column(name = "bitis_tarihi" , nullable = false)
    private LocalDateTime bitisTarihi;
}
