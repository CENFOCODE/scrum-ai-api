package com.project.demo.logic.entity.transcript;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.project.demo.logic.entity.ceremonySession.CeremonySession;
import com.project.demo.logic.entity.ceremonySession.CeremonySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TranscriptionService {

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private CeremonySessionRepository ceremonySessionRepository;

    @Value("${google.credentials.path:}")
    private String credentialsPath;
    private SpeechClient createSpeechClient() throws IOException {


        if (credentialsPath != null && !credentialsPath.isEmpty()) {

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new FileInputStream(credentialsPath)
            );

            SpeechSettings settings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();


            return SpeechClient.create(settings);
        } else {

            return SpeechClient.create();
        }
    }

    public String transcribeChunkOnly(String audioBase64) {
        try (SpeechClient speechClient = createSpeechClient()) {

            byte[] audioBytes = Base64.getDecoder().decode(audioBase64);
            ByteString audioData = ByteString.copyFrom(audioBytes);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                    .setSampleRateHertz(48000)
                    .setLanguageCode("es-MX")
                    .setEnableAutomaticPunctuation(true)
                    .setModel("latest_long")
                    .setUseEnhanced(true)
                    .setMaxAlternatives(1)
                    .setProfanityFilter(false)


                    .addSpeechContexts(
                            SpeechContext.newBuilder()
                                    .addPhrases("Scrum")
                                    .addPhrases("Sprint")
                                    .addPhrases("backlog")
                                    .addPhrases("Daily Scrum")
                                    .addPhrases("Sprint Planning")
                                    .addPhrases("Sprint Review")
                                    .addPhrases("Sprint Retrospective")
                                    .addPhrases("Product Backlog")
                                    .addPhrases("Sprint Backlog")
                                    .addPhrases("Definition of Done")
                                    .addPhrases("Scrum Master")
                                    .addPhrases("Development Team")
                                    .addPhrases("Stakeholder")
                                    .addPhrases("Epic")
                                    .addPhrases("Feature")
                                    .addPhrases("Burndown Chart")
                                    .addPhrases("Burnup Chart")
                                    .addPhrases("Backlog Refinement")
                                    .addPhrases("User Story")

                                    .addPhrases("Edwin")
                                    .addPhrases("Jose Daniel")
                                    .addPhrases("Keysha")
                                    .addPhrases("Daniel")
                                    .addPhrases("Nilce")
                                    .addPhrases("Daniela")
                                    .addPhrases("Olman")
                                    .addPhrases("Nelson")
                                    .setBoost(15.0f)
                                    .build()
                    )
                    .setEnableWordTimeOffsets(false)
                    .setEnableWordConfidence(false)


                    .setAudioChannelCount(1)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioData)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);

            StringBuilder fullTranscript = new StringBuilder();

            for (int i = 0; i < response.getResultsCount(); i++) {
                SpeechRecognitionResult result = response.getResults(i);

                if (result.getAlternativesCount() > 0) {
                    SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                    String transcript = alternative.getTranscript().trim();

                    if (!transcript.isEmpty()) {
                        fullTranscript.append(transcript).append(" ");
                    }
                }
            }
            return fullTranscript.toString().trim();
        } catch (Exception e) {
            System.err.println("Error en transcripción: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }


    /**
     *  Obtiene transcripts de una ceremony_session.
     */
    public List<Transcript> getCeremonyTranscripts(Long ceremonySessionId) {
        return transcriptRepository.findByCeremonySessionIdOrderByTimestampAsc(ceremonySessionId);
    }
    public void saveBatchTranscripts(BatchTranscriptRequest request) {
        CeremonySession ceremonySession = ceremonySessionRepository
                .findById(request.getCeremonySessionId())
                .orElseThrow(() -> new RuntimeException("CeremonySession no encontrada"));

        List<Transcript> transcripts = new ArrayList<>();

        for (BatchTranscriptRequest.TranscriptEntry entry : request.getTranscripts()) {
            Transcript transcript = new Transcript();
            transcript.setCeremonySession(ceremonySession);
            transcript.setUserId(entry.getUserId());
            transcript.setUsername(entry.getUsername());
            transcript.setText(entry.getText());
            transcript.setTimestamp(entry.getTimestamp());
            transcript.setRoomId(request.getRoomId());

            transcripts.add(transcript);

        }
        transcriptRepository.saveAll(transcripts);
    }

    public String getFormattedTranscript(Long ceremonySessionId) {
        List<Transcript> transcripts = getCeremonyTranscripts(ceremonySessionId);
        StringBuilder sb = new StringBuilder();

        for (Transcript t : transcripts) {
            sb.append("[").append(t.getUsername()).append("]: ")
                    .append(t.getText()).append("\n\n");
        }

        return sb.toString();
    }

}