package com.wen.network.errorhandler;

import retrofit2.HttpException;

public class ExceptionHandler {
    private static final int NOT_FOUND = 404;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case NOT_FOUND:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = e.getMessage();
        }
        return ex;
    }

    public class ERROR {
        public static final int HTTP_ERROR = 404;
        public static final int UNKNOWN = -404;
    }


    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String message;
    }
}
