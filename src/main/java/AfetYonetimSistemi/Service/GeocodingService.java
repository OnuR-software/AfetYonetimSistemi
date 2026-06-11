package AfetYonetimSistemi.Service;

import AfetYonetimSistemi.Exception.InvalidKoordinatException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "AfetYonetimSistemi/1.0");
            return execution.execute(request, body);
        });
    }

    public String getIl(double lat, double lng) {

        String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&accept-language=tr",
                lat, lng
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, String> address = (Map<String, String>) response.get("address");

        String il = address.getOrDefault("province", address.get("state"));

        if (il == null) throw new InvalidKoordinatException("Lütfen gecerli bir koordinat giriniz");
        return il.substring(0, 1).toUpperCase(new Locale("tr")) + il.substring(1).toLowerCase(new Locale("tr"));

    }
}
