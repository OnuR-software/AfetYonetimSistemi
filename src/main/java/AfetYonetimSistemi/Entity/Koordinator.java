package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "koordinatorler")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Koordinator extends User {

    @Column(name = "username" , nullable = false)
    private String username;

    @Column(name = "email" , nullable = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "il_id")
    private IL gorevYaptıgıİl;

}
