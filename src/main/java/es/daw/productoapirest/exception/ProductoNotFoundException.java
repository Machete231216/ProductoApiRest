package es.daw.productoapirest.exception;

public class ProductoNotFoundException extends RuntimeException{ //sera con runtimeexception
    public ProductoNotFoundException(String message){
        super(message);
    }
}
