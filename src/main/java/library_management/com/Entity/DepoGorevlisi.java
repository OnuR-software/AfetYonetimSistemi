package library_management.com.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "depo_gorevlileri")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DepoGorevlisi extends User {

    @Column(name = "username" , nullable = false)
    private String username;

    @Column(name = "email" , nullable = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "depo_id")
    private Depo depo;

    @OneToOne
    @JoinColumn(name = "il_id")
    private IL gorevYaptıgıIl ;

}
