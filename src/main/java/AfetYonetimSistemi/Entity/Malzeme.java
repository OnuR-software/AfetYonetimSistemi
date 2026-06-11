package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import AfetYonetimSistemi.Model.MalzemeKategori;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "malzemeler")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class Malzeme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "malzeme_adi" , nullable = false)
    private String malzemeAdi;

    @Column(name = "malzeme_kategori" , nullable = false)
    @Enumerated(EnumType.STRING)
    private MalzemeKategori malzemeKategori;

    @Column(name = "yetecek_kisi_sayisi" , nullable = false)
    private Integer yetecekKisiSayisi;

    @Column(name = "aktif" , nullable = false)
    @ColumnDefault("true")
    private boolean aktif = true;
}
