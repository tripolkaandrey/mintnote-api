package com.github.tripolkaandrey.mintnoteapi.service;

import com.github.tripolkaandrey.mintnoteapi.exception.InvalidLanguageCodeException;
import com.github.tripolkaandrey.mintnoteapi.exception.TranslationServiceInternalError;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.translate.v3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<String> translate(String text, String targetLanguage) {
        try (var client = TranslationServiceClient.create(translationServiceSettings)) {
            var parent = LocationName.of(gcpProjectIdProvider.getProjectId(), "global");

            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(text)
                            .build();

            return Mono.just(client.translateText(request))
                    .flatMapIterable(TranslateTextResponse::getTranslationsList).collect(
                            StringBuilder::new,
                            (StringBuilder collector, Translation translation) -> collector.append(translation.getTranslatedText()))
                    .map(StringBuilder::toString);
        } catch (InvalidArgumentException ex) {
            return Mono.error(InvalidLanguageCodeException::new);
        } catch (IOException ex) {
            //problem occurred with translation. Most probably referred to credentials
            return Mono.error(TranslationServiceInternalError::new);
        }
    }
}