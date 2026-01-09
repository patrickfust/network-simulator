package dk.fust.networksimulator.service;

import dk.fust.networksimulator.service.proxy.ProxyResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.util.Locale;

@Slf4j
@Service
public class ThrottleService {

    @Value("${network-simulator.throttling.chunk-size}") // Configurable bytes per second, default 1 MB
    private int chunkSize;

    public StreamingResponseBody makeThrottledResponseStream(ProxyResponse proxyResponse, long bytesPerSecond) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(bytesPerSecond)
                .refillIntervally(bytesPerSecond, Duration.ofSeconds(1))
                .build();
        Bucket responseBucket = Bucket.builder()
                .addLimit(bandwidth)
                .build();
        return outputStream -> {
            byte[] body = proxyResponse.getBody();
            if (body != null && body.length > 0) {
                log.debug("Starting to send response ({} bytes) with throttling at {} bytes/second", body.length, bytesPerSecond);
                long startTime = System.currentTimeMillis();
                long bytesSent = 0;
                for (int i = 0; i < body.length; i += chunkSize) {
                    int end = Math.min(i + chunkSize, body.length);
                    byte[] chunk = new byte[end - i];
                    System.arraycopy(body, i, chunk, 0, chunk.length);
                    try {
                        // log expected progress after this chunk would be sent
                        double expectedProgress = ((double) (bytesSent + chunk.length) / body.length) * 100.0;
                        String progressStr = String.format(Locale.ENGLISH, "%.2f", expectedProgress);
                        log.info("Attempting to consume {} bytes from the bucket. Progress: {} %", chunk.length, progressStr);

                        responseBucket.asBlocking().consume(chunk.length); // Throttles here
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupt status
                        break; // Exit the loop to stop streaming
                    }
                    outputStream.write(chunk);
                    outputStream.flush();
                    bytesSent += chunk.length;
                }
                log.info("Sent total of {} bytes in {} ms", bytesSent, System.currentTimeMillis() - startTime);
            }
        };
    }

}
