package com.github.tripolkaandrey.mintnoteapi.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.translate.v3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public final class TranslationService {
    private final GcpProjectIdProvider gcpProjectIdProvider;
    private final TranslationServiceSettings translationServiceSettings;

    public TranslationService(GcpProjectIdProvider gcpProjectIdProvider,
                              @Value("${translation-api-user-service-account}") String serviceAccount) throws IOException {
        this.gcpProjectIdProvider = gcpProjectIdProvider;
        this.translationServiceSettings =
                TranslationServiceSettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(
                                        GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccount.getBytes())))).build();
    }

    public String translate(String text, String targetLanguage) throws IOException {
        var result = new StringBuilder();

        try (var client = TranslationServiceClient.create(translationServiceSettings)) {
            var parent = LocationName.of(gcpProjectIdProvider.getProjectId(), "global");

            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(text)
                            .build();

            TranslateTextResponse response = client.translateText(request);

            for (Translation translation : response.getTranslationsList()) {
                result.append(translation.getTranslatedText());
            }

            return result.toString();
        }
    }
}