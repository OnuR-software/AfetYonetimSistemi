package AfetYonetimSistemi.Model;

public enum AfetTuru {
    DEPREM,   // şiddet: magnitude (AFAD API)
    SEL,      // şiddet: su seviyesi (MGM API)
    YANGIN,   // şiddet: hektar (OGM API)
    FIRTINA,  // şiddet: rüzgar hızı (MGM API)
    HEYELAN
}
