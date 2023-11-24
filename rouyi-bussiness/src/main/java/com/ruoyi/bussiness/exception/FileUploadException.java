package com.ruoyi.bussiness.exception;

import com.ruoyi.bussiness.domain.TextToSpeechResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author 12290
 */
@ControllerAdvice
public class FileUploadException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<TextToSpeechResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e){
        return ResponseEntity.badRequest().body(new TextToSpeechResponse("Upload file too large."));
    }
}
