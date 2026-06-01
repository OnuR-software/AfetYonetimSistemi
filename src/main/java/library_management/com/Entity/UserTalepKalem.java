package library_management.com.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Kaynak Talepler_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTalepKalem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_talep_id")
    private UserTalep userTalep;

    @ManyToOne
    @JoinColumn(name = "malzeme_id")
    private Malzeme malzeme;

}
