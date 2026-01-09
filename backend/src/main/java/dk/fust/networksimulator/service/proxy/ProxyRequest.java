package dk.fust.networksimulator.service.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "body")
public class ProxyRequest {

    private String targetBaseUrl;
    private String path;
    private String method;
    private byte[] body;
    private Map<String, String> headers;
    private long timeoutMillis;
    private boolean followRedirect;

}
