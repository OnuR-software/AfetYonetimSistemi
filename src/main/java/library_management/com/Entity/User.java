package library_management.com.Entity;

import jakarta.persistence.*;
import library_management.com.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)


public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(unique = true , name = "uuid" , nullable = false , updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "password" , nullable = false)
    private String password;

    @Column(name = "tc" , unique = true , nullable = false)
    private String tc;

    @Column(name = "telNo" , unique = true , nullable = false)
    private String telNo;

    @Column(name = "role" , nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "pin_sayisi" , nullable = false)
    private Integer pinSayısı = 0;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    private List<Pinler> pinler;

    @Column(name = "ilk_giris" , nullable = false)
    private boolean ilkGiris = false;

    @Column(name = "kod")
    private String kod;

    @Column(name = "kod_gecerlilik_suresi")
    private LocalDateTime kodGecerlilikSuresi;
}
