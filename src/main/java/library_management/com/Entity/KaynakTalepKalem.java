package library_management.com.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kaynak_talep_kalemleri")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KaynakTalepKalem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kaynak_talep_id")
    private KaynakTalep kaynakTalep;

    @ManyToOne
    @JoinColumn(name = "malzeme_id")
    private Malzeme malzeme;

    @Column(name = "miktar" , nullable = false)
    private Integer miktar;

}
