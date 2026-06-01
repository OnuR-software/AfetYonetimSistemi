package library_management.com.Entity;

import jakarta.persistence.*;
import library_management.com.Model.DepoModeli;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.List;

@Entity
@Data
@Table(name = "depolar")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
public class Depo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "depo_adi" , nullable = false)
    private String depoAdi;

    @Column(name = "depo_model" , nullable = false)
    private DepoModeli depoModeli ;

    @Column(name = "aktif" , nullable = false)
    @ColumnDefault("true")
    private boolean aktif = true;

    @OneToMany(mappedBy = "depo" , cascade = CascadeType.ALL , orphanRemoval = true)
    @NotAudited
    private List<DepoMalzeme> depoMalzeme;

    @ManyToOne
    @JoinColumn(name = "il_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private IL il;

    @Column(name = "latitude" , nullable = false)
    private double latitude;

    @Column(name = "longitude" , nullable = false)
    private double longitude;

}
