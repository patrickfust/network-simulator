package dk.fust.networksimulator.service.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyResponse {

    private HttpStatusCode statusCode;
    private byte[] body;
    private String contentType;
    private HttpHeaders headers;

}
