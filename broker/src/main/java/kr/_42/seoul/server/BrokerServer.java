package kr._42.seoul.server;

import kr._42.seoul.client.Request;
import kr._42.seoul.client.Response;

public interface BrokerServer {
    public final ResponseStatusCode SUCCESS = ResponseStatusCode.SUCCESS;
    public final ResponseStatusCode FAILURE = ResponseStatusCode.FAILURE;

    void order(Request request);

    Response query(Request request);

}
