package com.example.blog_system_backend.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final WebClient webClient;
    private final String baseUrl;
    private final String email;
    private final String password;
    private final String bearerToken;

    public ImageService(
            WebClient webClient,
            @Value("${image.foofish.base-url:https://image.foofish.work/api/v1}") String baseUrl,
            @Value("${image.foofish.email:}") String email,
            @Value("${image.foofish.password:}") String password,
            @Value("${image.foofish.token:}") String bearerToken
    ) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.email = email;
        this.password = password;
        this.bearerToken = bearerToken;
    }

    public ImageUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file must not be empty");
        }

        String resolvedToken = bearerToken;
        if (resolvedToken == null || resolvedToken.isBlank()) {
            resolvedToken = loginAndGetToken();
        }
        String authToken = resolvedToken;

        try {
            ImageUploadResponse response = webClient.post()
                    .uri(baseUrl + "/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(authToken))
                    .body(BodyInserters.fromMultipartData(buildMultipart(file)))
                    .retrieve()
                    .bodyToMono(ImageUploadResponse.class)
                    .block();

            if (response == null) {
                throw new IllegalStateException("upload response was empty");
            }
            if (!response.status()) {
                throw new IllegalStateException(response.message());
            }
            return response;
        } catch (WebClientResponseException ex) {
            String message = ex.getResponseBodyAsString();
            throw new IllegalStateException("upload failed: " + message, ex);
        }
    }

    private String loginAndGetToken() {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("image.foofish.token or image.foofish.email/password must be configured");
        }

        ImageTokenResponse response;
        try {
            response = webClient.post()
                    .uri(baseUrl + "/tokens")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(new ImageTokenRequest(email, password))
                    .retrieve()
                    .bodyToMono(ImageTokenResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            String message = ex.getResponseBodyAsString();
            throw new IllegalStateException("token request failed: " + message, ex);
        }

        if (response == null || !response.status() || response.data() == null || response.data().token() == null) {
            throw new IllegalStateException("token response invalid");
        }

        return response.data().token();
    }

    private MultiValueMap<String, Object> buildMultipart(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();
        byte[] content = toBytes(file);

        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new org.springframework.core.io.ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        });
        return form;
    }

    private byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception ex) {
            throw new IllegalStateException("failed to read file", ex);
        }
    }
}
