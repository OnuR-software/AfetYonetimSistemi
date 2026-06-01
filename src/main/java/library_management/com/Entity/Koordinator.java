package library_management.com.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "İl Koordinatorleri")
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
