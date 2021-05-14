package com.github.tripolkaandrey.mintnoteapi.service;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.translate.v3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public final class TranslationService {
    private final GcpProjectIdProvider gcpProjectIdProvider;

    public TranslationService(GcpProjectIdProvider gcpProjectIdProvider) {
        this.gcpProjectIdProvider = gcpProjectIdProvider;
    }

    public String translate(String text, String targetLanguage) throws IOException {
        var result = new StringBuilder();

        try (var client = TranslationServiceClient.create()) {
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
