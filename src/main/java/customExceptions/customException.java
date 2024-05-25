package customExceptions;

public class customException extends Exception {

    // We make a default constructor for the customException class
    public customException(){
        super("An unspecified error has occurred..");
    }

    // This constructor allows us to define the string to output to our custom error messages
    public customException(String error_msg){
        super(error_msg);
    }
}
