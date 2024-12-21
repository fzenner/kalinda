package com.kewebsi.errorhandling;

public class ErrorHelper {

    public static long parseClientDataToLong(String value) throws ExpectedClientDataError {

        try {
            long result = Long.parseLong(value);
            return result;
        } catch (Exception e) {
            throw new ExpectedClientDataError(e);
        }
    }

    public static boolean debbugingIsActive() {
        return true;
    }

}
