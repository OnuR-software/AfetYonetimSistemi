package library_management.com.Entity;

import jakarta.persistence.*;
import library_management.com.Model.AfetTuru;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "afetler")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Afet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "afet_turu" , nullable = false)
    private AfetTuru afetTuru;

}
