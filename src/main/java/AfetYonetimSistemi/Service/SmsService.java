package AfetYonetimSistemi.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.type.PhoneNumber;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String phoneNumber;

    public void smsDogrulamaKoduGonder(String telNo, String kod) {
        Twilio.init(accountSid, authToken);

        Message.creator(
                new PhoneNumber(telNo),
                new PhoneNumber(phoneNumber),
                "Doğrulama kodunuz: " + kod
        ).create();
    }
}
