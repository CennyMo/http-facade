package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract class BaseHttpClient implements HttpClient {
    private Duration timeout = Duration.ofSeconds(30);

    public BaseHttpClient(HttpClientConfig config) {
        if (config.timeout() != null) {
            this.timeout = config.timeout();
        }
    }

    /**
     * Send a request synchronously with the configured timeout.
     *
     * @param request The HTTP request to send.
     * @return The HTTP response.
     */
    @Override
    public HttpResponse sendSync(HttpRequest request) {
        try {
            // Wait for the async operation to complete within the specified timeout
            CompletableFuture<HttpResponse> future = this.send(request);
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);  // Use the configured timeout
        } catch (TimeoutException e) {
            throw new HttpClientException("Request timed out after " + timeout.toSeconds() + " seconds", e);
        } catch (Exception e) {
            throw new HttpClientException("Failed to execute synchronous request", e);
        }
    }
}
