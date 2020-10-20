package com.intex.weightterminal.exception;

public class WeightTerminalException extends Throwable {
    private static final long serialVersionUID = -597432281485726720L;

    private final ErrorCode error;

    public WeightTerminalException(ErrorCode error) {
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}
