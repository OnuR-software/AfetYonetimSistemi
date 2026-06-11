package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import AfetYonetimSistemi.Model.KaynakTuru;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stok_girisi")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StokGiris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tarih")
    private LocalDateTime tarih;

    @Column(name = "miktar")
    private Integer miktar;

    @ManyToOne
    @JoinColumn(name = "depo_gorevlisi_id")
    private DepoGorevlisi depoGorevlisi;

    @Column(name = "kaynak_turu")
    private KaynakTuru kaynakTuru ;

    @Column(name = "Bagıscı_Adı")
    private String BagıscıAdı;

    @ManyToOne
    @JoinColumn(name = "malzeme_id")
    private Malzeme malzeme;
}
