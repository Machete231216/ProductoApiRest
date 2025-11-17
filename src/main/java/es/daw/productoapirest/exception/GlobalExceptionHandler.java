package es.daw.productoapirest.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final ConversionService conversionService;

    public GlobalExceptionHandler(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Error de constraint violation!");
        errorDTO.setTimestamp(LocalDateTime.now());

        Map<String,String> error = new HashMap<>();
        //error.put(ex.getConstraintName(), ex.getErrorMessage());
        ex.getConstraintViolations().forEach(
                constraintViolation -> error.put(
                        constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage())
        );
        errorDTO.setDetails(error);

        return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);

    }

    //@ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(MethodArgumentNotValidException ex){



/*
        List<String> errores = ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList();
*/

        Map<String,String> errores = new HashMap<>();
        for(FieldError e : ex.getBindingResult().getFieldErrors()){ //un FieldError devuelve clave y valor
            errores.put(e.getField(),e.getDefaultMessage());
        }

        ErrorDTO errorDTO = new ErrorDTO(
                "Error de validación",
                LocalDateTime.now(),
                errores

        );

        //Devolvemosun error 400
        // return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException (Exception ex){
        Map<String,String> errores = new HashMap<>();
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Pedazo de error genérico");
        errorDTO.setTimestamp(LocalDateTime.now());

        errores.put("errorGenerico",ex.getMessage());
        errorDTO.setDetails(errores);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorDTO> handlerNumberFormatException(NumberFormatException ex){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(LocalDateTime.now());
        errorDTO.setMessage("Formato de numero invalido");
        errorDTO.setDetails(new HashMap<>());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleProductoNotFoundException(ProductoNotFoundException ex){
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(Map.of(
                        "exception", ex.getClass().getSimpleName(),
                        "message", ex.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);//404
    }

    //Cotrolar SQLException

}
