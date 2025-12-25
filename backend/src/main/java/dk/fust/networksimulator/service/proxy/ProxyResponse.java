package dk.fust.networksimulator.service.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "body")
public class ProxyResponse {

    private HttpStatusCode statusCode;
    private byte[] body;
    private String contentType;
    private HttpHeaders headers = new HttpHeaders();

}
