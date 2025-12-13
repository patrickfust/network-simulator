package dk.fust.networksimulator.service;

import dk.fust.networksimulator.service.proxy.ClientHttpRequestFactory;
import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ProxyService {

    public void sendRequest(ProxyRequest proxyRequest, ProxyResponse proxyResponse) {
        HttpHeaders httpHeaders = asSpringHeaders(proxyRequest.getHeaders());
        // This way we tell the target application, who the call originated from
        String host = proxyRequest.getHeaders().get("host");
        if (host != null) {
            httpHeaders.add("X-Forwarded-Host", host);
            httpHeaders.add("Forwarded", "host=" + host);
        }

        RestClient restClient = buildRestClient(proxyRequest);
        RestClient.RequestBodySpec builder = restClient
                .method(HttpMethod.valueOf(proxyRequest.getMethod()))
                .uri(proxyRequest.getPath())
                .headers(headers -> headers.addAll(httpHeaders));
        if (proxyRequest.getBody() != null) {
            builder.body(proxyRequest.getBody());
        }
        builder
                .exchange((request, response) -> {
                    log.info("Proxied request to {} {}, received status code {}",
                            proxyRequest.getMethod(),
                            proxyRequest.getPath(),
                            response.getStatusCode());
                    populateProxyResponse(response, proxyResponse);
                    return proxyResponse;
                });
    }

    private void populateProxyResponse(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response, ProxyResponse proxyResponse) throws IOException {
        proxyResponse.setStatusCode(response.getStatusCode());
        proxyResponse.setBody(response.getBody().readAllBytes());
        proxyResponse.setHeaders(response.getHeaders());
        proxyResponse.setContentType(response.getHeaders().getContentType() != null ? response.getHeaders().getContentType().toString() : null);
        proxyResponse.setHeaders(response.getHeaders());
    }

    private HttpHeaders asSpringHeaders(Map<String, String> incomingHeaders) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, String> entry : incomingHeaders.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        return httpHeaders;
    }

    private RestClient buildRestClient(ProxyRequest proxyRequest) {
        Duration timeout = Duration.ofMillis(proxyRequest.getTimeoutMillis());
        ClientHttpRequestFactory factory = new ClientHttpRequestFactory(proxyRequest.isFollowRedirect());
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(proxyRequest.getTargetBaseUrl())
                .build();
    }

}
