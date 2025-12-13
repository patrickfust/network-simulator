package dk.fust.networksimulator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;

@Configuration
public class SwaggerConfig {

    private static final String SWAGGER_INDEX = "/_/swagger-ui/index.html";

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Network Simulator API")
                        .description("API for managing and simulating scenarios")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("network-simulator")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerRedirect() {
        return makeRedirect("/_/");
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerRedirectInSwagger() {
        return makeRedirect("/_/swagger-ui");
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerRedirectInSwaggerWithSlash() {
        return makeRedirect("/_/swagger-ui/");
    }

    private RouterFunction<ServerResponse> makeRedirect(String path) {
        return RouterFunctions.route(GET(path), req -> ServerResponse.permanentRedirect(URI.create(SWAGGER_INDEX)).build());
    }

}
