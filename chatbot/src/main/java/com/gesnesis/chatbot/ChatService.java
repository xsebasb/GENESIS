package com.gesnesis.chatbot;

import cloud.genesys.webmessaging.sdk.GenesysCloudRegionWebSocketHosts;
import cloud.genesys.webmessaging.sdk.WebMessagingClient;
import cloud.genesys.webmessaging.sdk.model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatService {

    private WebMessagingClient client;
    private String token;  // UUID para la sesión
    private CompletableFuture<Void> connectionFuture = new CompletableFuture<>();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostConstruct
    public void init() {
        WebMessagingClient.SessionListener sessionListener = new WebMessagingClient.SessionListener() {
            @Override
            public void sessionResponse(SessionResponse sessionResponse, String s) {
                System.out.println("Session response received: " + s);
            }

            @Override
            public void structuredMessage(StructuredMessage structuredMessage, String s) {
                System.out.println("Received structured message: " + s);

                // Simulamos enviar este mensaje al frontend
                sendToWebSocket(structuredMessage, s);
            }


            @Override
            public void presignedUrlResponse(PresignedUrlResponse presignedUrlResponse, String s) {
                System.out.println("Presigned URL response received: " + s);
            }

            @Override
            public void uploadSuccessEvent(UploadSuccessEvent uploadSuccessEvent, String s) {
                System.out.println("Upload was successful: " + s);
            }

            @Override
            public void uploadFailureEvent(UploadFailureEvent uploadFailureEvent, String s) {
                System.out.println("Upload failed: " + s);
            }

            @Override
            public void connectionClosedEvent(ConnectionClosedEvent connectionClosedEvent, String s) {
                System.out.println("Connection was closed: " + s);
            }

            @Override
            public void sessionExpiredEvent(SessionExpiredEvent sessionExpiredEvent, String s) {
                System.out.println("Session expired: " + s);
            }

            @Override
            public void sessionClearedEvent(SessionClearedEvent sessionClearedEvent, String s) {

            }

            @Override
            public void jwtResponse(JwtResponse jwtResponse, String s) {
                System.out.println("JWT response received: " + s);
            }

            @Override
            public void unexpectedMessage(BaseMessage baseMessage, String s) {
                System.out.println("Unexpected message received: " + s);
            }

            @Override
            public void webSocketConnected() {
                System.out.println("WebSocket connected.");
                connectionFuture.complete(null);  // Completa el CompletableFuture cuando la conexión se establece
            }

            @Override
            public void webSocketDisconnected(int i, String s) {
                System.out.println("WebSocket disconnected with status: " + i + ", reason: " + s);
            }

            @Override
            public void webSocketError(String reason) {
                System.out.println("WebSocket encountered an error: " + reason);
            }

        };

        GenesysCloudRegionWebSocketHosts webSocketHost = GenesysCloudRegionWebSocketHosts.us_east_1;
        client = new WebMessagingClient(webSocketHost);
        client.addSessionListener(sessionListener);

        String deploymentId = "cae51d61-033e-48c4-a73c-a27abef96fd5";
        String origin = "app.mypurecloud.com"; 
        client.connect(deploymentId, origin);

        // Espera hasta que la conexión esté establecida
        connectionFuture.join();

        // Una vez que esté conectado, procede a configurar la sesión
        configureSession(deploymentId);

        //new Thread(this::startUserInputListener).start();

    }

   /* private void startUserInputListener() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Please type a message:");
                String userMessage = scanner.nextLine();  // Bloquea hasta que el usuario escribe algo

                // Enviar mensaje a través de WebSocket
                sendMessage(userMessage);
            }
        }
    }*/
    private void configureSession(String deploymentId) {
        this.token = UUID.randomUUID().toString();

        // Utilizando el método con dos parámetros: deploymentId y token.
        client.configureSession(deploymentId, this.token);
        System.out.println("Configured session with deploymentId: " + deploymentId + " and token: " + this.token);
    }
    public void sendToWebSocket(StructuredMessage structuredMessage, String s) {
        // Convertir el StructuredMessage a un formato adecuado para el frontend si es necesario.
        // Por ahora, simplemente enviamos el String 's'
        messagingTemplate.convertAndSend("/topic/messages", s);
    }
    public String sendMessage(String messageText) {
        try {

            client.sendMessage(messageText);
            return messageText;  // Representaría la respuesta, que deberías obtener del SDK.
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
            // Lógica de manejo de errores adicional si es necesario
            return null;
        }
    }

}

