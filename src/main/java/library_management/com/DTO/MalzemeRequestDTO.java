package library_management.com.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import library_management.com.Model.MalzemeKategori;


public class MalzemeRequestDTO {

    @NotBlank(message = "İd boş olamaz")
    @Min(value = 1 , message = "İd pozitif bir sayı olmak zorundadır")
    private Long id;

    @NotBlank(message = "Malzeme ismi boş olamaz")
    private String malzemeAdi;

    @NotBlank(message = "Malzeme kategorisi secilmek zorundadır")
    private MalzemeKategori malzemeKategori;


}
