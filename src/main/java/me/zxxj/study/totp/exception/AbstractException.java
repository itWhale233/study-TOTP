package me.zxxj.study.totp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 通用异常
 */
public abstract class AbstractException extends RuntimeException {

    /**
     * Error errorData.
     */
    private Object errorData;

    public AbstractException() {
        super();
    }

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Http status code
     *
     * @return {@link HttpStatus}
     */
    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    /**
     * Sets error errorData.
     *
     * @param errorData error data
     * @return current exception.
     */
    @NonNull
    public AbstractException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}
