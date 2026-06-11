package AfetYonetimSistemi.Log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

@Entity
@RevisionEntity(AuditRevisionListener.class)
@Data
public class AuditRevision {

    @Id
    @GeneratedValue
    @RevisionNumber
    private Long id;

    @RevisionTimestamp
    @Column(name = "tarih")
    private LocalDateTime tarih;

    @Column(name = "yapan_kisi")
    private String yapanKisi;
}
