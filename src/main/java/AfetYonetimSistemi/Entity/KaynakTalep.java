package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import AfetYonetimSistemi.Model.Oncelik;
import AfetYonetimSistemi.Model.TransferDurumu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kaynak_talepler")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KaynakTalep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "koordinator_id")
    private Koordinator koordinator;

    @Column(name = "oncelik" , nullable = false)
    @Enumerated(EnumType.STRING)
    private Oncelik oncelik;

    @Column(name = "transfer_durumu" , nullable = false)
    @Enumerated(EnumType.STRING)
    @Default
    private TransferDurumu transferDurumu = TransferDurumu.BEKLEMEDE;

    @Column(name = "aciklama" )
    private String acıklama;

    @Column(name = "admin_notu")
    private String adminNotu;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "olusturulma_tarihi" , nullable = true)
    private LocalDateTime Olusturulma_tarihi;

    @Column(name = "guncelleme_tarihi" , nullable = true)
    private LocalDateTime Guncelleme_tarihi;

    @OneToMany(mappedBy = "kaynakTalep" , cascade = CascadeType.ALL)
    private List<KaynakTalepKalem> kalemler;

    @Column(name = "aktif" , nullable = false)
    @ColumnDefault("true")
    @Default
    private boolean aktif = true;
    @ManyToOne
    @JoinColumn(name = "yardım_alan_depo")
    private Depo yardımAlanDepo;

    @ManyToOne
    @JoinColumn(name = "yardım_goturen_depo")
    private Depo yardımGoturenDepo;
}
