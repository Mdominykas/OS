package OSException;

public class NotEnoughFreePagesException extends OSException{
    public NotEnoughFreePagesException(String errorMessage){
        super(errorMessage);
    }
}
