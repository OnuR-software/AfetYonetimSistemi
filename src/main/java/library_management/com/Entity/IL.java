package library_management.com.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Entity
@Table(name = "iller")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "il_adi" , nullable = false , unique = true)
    private String ilAdi;

    @Column(name = "il_kodu" , nullable = false , unique = true)
    private String ilKodu;

    @OneToMany(mappedBy = "il")
    private List<Depo> depoList;


}
