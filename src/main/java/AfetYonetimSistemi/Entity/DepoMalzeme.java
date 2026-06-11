package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import AfetYonetimSistemi.Model.MalzemeDurumu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "depo_malzemeler")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepoMalzeme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "malzeme_id")
    private Malzeme malzeme;

    @ManyToOne
    @JoinColumn(name = "depo_id")
    private Depo depo;

    @Column(name = "stok_miktari")
    private Integer stokMiktari;

    @Column(name = "rezerve_stok_miktari")
    private Integer rezerveStokMiktari;

    public int KullanılabilirStokMiktari () {
        return stokMiktari - rezerveStokMiktari;
    }

    @Column(name = "malzeme_durumu")
    @Enumerated(EnumType.STRING)
    private MalzemeDurumu malzemeDurumu;

    public void updateMalzemeDurumu() {
        int kullanilabilir = KullanılabilirStokMiktari();
        if (kullanilabilir < 300) {
            this.malzemeDurumu = MalzemeDurumu.KRITIK;
        } else if (kullanilabilir < 1000) {
            this.malzemeDurumu = MalzemeDurumu.NORMAL;
        } else {
            this.malzemeDurumu = MalzemeDurumu.YUKSEK;
        }
    }
}
