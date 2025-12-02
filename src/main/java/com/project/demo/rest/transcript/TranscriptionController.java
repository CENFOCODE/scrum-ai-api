package com.project.demo.rest.transcript;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.transcript.BatchTranscriptRequest;
import com.project.demo.logic.entity.transcript.Transcript;
import com.project.demo.logic.entity.transcript.TranscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {

    @Autowired
    private TranscriptionService transcriptionService;


    @PostMapping("/transcribe-chunk")
    public ResponseEntity<?> transcribeChunk(@RequestBody Map<String, String> request, HttpServletRequest req) {

        String audioBase64 = request.get("audioBase64");
        String text = transcriptionService.transcribeChunkOnly(audioBase64);
        return new GlobalResponseHandler().handleResponse(
                "Transcripción completada",
                text,
                HttpStatus.OK,
                req
        );
    }



    @PostMapping("/save-batch")
    public ResponseEntity<?> saveBatch(@RequestBody BatchTranscriptRequest request, HttpServletRequest req) {
        try {
            transcriptionService.saveBatchTranscripts(request);

            return new GlobalResponseHandler().handleResponse(
                    "Transcripts guardados exitosamente",
                    HttpStatus.OK,
                    req
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al guardar transcripts: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    req
            );
        }
    }
}