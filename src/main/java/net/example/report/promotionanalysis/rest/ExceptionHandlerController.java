package net.example.report.promotionanalysis.rest;

import lombok.extern.slf4j.Slf4j;
import net.example.report.promotionanalysis.exception.ChainNotFoundException;
import net.example.report.promotionanalysis.exception.PriceRegistrationException;
import net.example.report.promotionanalysis.exception.ProductNotFoundException;
import net.example.report.promotionanalysis.model.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ChainNotFoundException.class,
            ProductNotFoundException.class,
            PriceRegistrationException.class})
    public ErrorDto processingException(RuntimeException ex) {
        return errorMessage(ex);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto processingException(MethodArgumentNotValidException ex) {
        return errorMessage(ex);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto processingException(Exception ex) {
        return errorMessage(ex);
    }

    private ErrorDto errorMessage(Exception ex) {
        log.error(ex.getMessage());
        return new ErrorDto(ex.getMessage());
    }
}
