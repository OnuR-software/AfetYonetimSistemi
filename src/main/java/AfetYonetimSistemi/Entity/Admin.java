package AfetYonetimSistemi.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Column(name = "username" , nullable = false)
    private String username;

    @Column(name = "email" , nullable = true)
    private String email;

    private String telNo;

}
