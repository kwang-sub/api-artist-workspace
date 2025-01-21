package org.example.workspace.controller.advice;

import lombok.RequiredArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.response.FieldErrorResDto;
import org.example.workspace.exception.MessageArgumentException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorResDto> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldErrorResDto(f.getObjectName(), f.getField(), f.getDefaultMessage()))
                .toList();
        String detail = Optional.ofNullable(result.getGlobalError())
                .map(ObjectError::getDefaultMessage)
                .orElseGet(() -> fieldErrors.isEmpty() ? ex.getMessage() : fieldErrors.get(0).message());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setProperty(ApplicationConstant.ExceptionHandler.FIELD_ERROR_KEY, fieldErrors);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, getMessage(e, null));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, getMessage(e, null));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception e) {
        e.printStackTrace();
        HttpStatusCode status;
        ProblemDetail problem;
        try {
            status = HttpStatus.BAD_REQUEST;

            String message;
            if (e instanceof MessageArgumentException) {
                MessageArgumentException argException = (MessageArgumentException) e;
                message = getMessage(e, argException.getArgs());
            } else {
                message = getMessage(e, null);
            }

            problem = ProblemDetail.forStatusAndDetail(status, message);
        } catch (NoSuchMessageException name) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            problem = ProblemDetail.forStatusAndDetail(status, "관리자에게 문의 바랍니다.");
        }

        return ResponseEntity.status(status).body(problem);
    }

    private String getMessage(Exception e, Object[] args) {
        String code = "error." + e.getClass().getName();
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
