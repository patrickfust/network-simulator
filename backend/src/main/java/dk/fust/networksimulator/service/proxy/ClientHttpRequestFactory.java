package dk.fust.networksimulator.service.proxy;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    private final boolean followRedirects;

    public ClientHttpRequestFactory(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        super.prepareConnection(connection, httpMethod);
        connection.setInstanceFollowRedirects(followRedirects);
    }

}
