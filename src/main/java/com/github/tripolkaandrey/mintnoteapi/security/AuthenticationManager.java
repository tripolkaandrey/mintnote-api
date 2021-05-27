package com.github.tripolkaandrey.mintnoteapi.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public final class AuthenticationManager implements ReactiveAuthenticationManager {
    public AuthenticationManager(GcpProjectIdProvider gcpProjectIdProvider,
                                 @Value("${service-account.firebase-auth-viewer}") String serviceAccount) throws IOException {
        var firebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccount.getBytes())))
                .setProjectId(gcpProjectIdProvider.getProjectId())
                .build();

        FirebaseApp.initializeApp(firebaseOptions);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var authToken = authentication.getCredentials().toString();

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(authToken);
            String userId = decodedToken.getUid();

            var auth = new UsernamePasswordAuthenticationToken(userId, decodedToken.getClaims(), null);
            return Mono.just(auth);
        } catch (FirebaseAuthException e) {
            return Mono.empty();
        }
    }
}