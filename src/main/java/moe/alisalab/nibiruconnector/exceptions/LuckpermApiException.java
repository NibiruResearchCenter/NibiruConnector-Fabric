package moe.alisalab.nibiruconnector.exceptions;

public class LuckpermApiException extends Exception {

    public final String reason;

    public LuckpermApiException(String reason) {
        this.reason = reason;
    }

}
