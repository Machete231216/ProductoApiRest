package es.daw.productoapirest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {
    private String message; //mensaje nuestro personlizado

    //private Date timestamp; trabajad con fechas
    private LocalDateTime timestamp;

    //detail un Map con campo valor
    private Map<String,String> details;
    //private String details; //e.getMessage

}
