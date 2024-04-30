package kr._42.seoul.client;

import kr._42.seoul.server.ResponseStatusCode;

public class Response {
    private final ResponseStatusCode responseStatusCode;
    private final Object data;
    private final String message;

    public Response(ResponseStatusCode responseStatusCode, Object data, String message) {
        this.responseStatusCode = responseStatusCode;
        this.data = data;
        this.message = message;
    }

    public ResponseStatusCode getResponseStatusCode() {
        return responseStatusCode;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

}
