package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
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
@Table(name = "kullanici_talepler")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTalep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "transfer_durumu")
    @Enumerated(EnumType.STRING)
    @Default
    private TransferDurumu transferDurumu = TransferDurumu.BEKLEMEDE;

    @ManyToOne
    @JoinColumn(name = "koordinator_id")
    private Koordinator koordinator;

    @Column(name = "aciklama")
    private String aciklama;

    @Column(name = "olusturulma_tarihi" , nullable = true)
    private LocalDateTime Olusturulma_tarihi;

    @Column(name = "guncelleme_tarihi" , nullable = true)
    private LocalDateTime Guncelleme_tarihi;

    @OneToMany(mappedBy = "userTalep" , cascade = CascadeType.ALL)
    private List<UserTalepKalem> kalemler;

    @Column(name = "aktif" , nullable = false)
    @ColumnDefault("true")
    @Default
    private boolean aktif = true;

    @ManyToOne
    @JoinColumn(name = "il_id")
    private IL mevcutIl;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pinler_id")
    private Pinler pinler;

    @Column(name = "kisiSayısı" , nullable = false)
    private Integer kisiSayısı;

    @ManyToOne
    @JoinColumn(name = "depo_id")
    private Depo depo; // yardımın hangi depo tarafından karsılandıgı
}
