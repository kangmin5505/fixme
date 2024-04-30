package kr._42.seoul.server;

import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.ResponseStatusCode;

public interface BrokerServer {
    public final ResponseStatusCode SUCCESS = ResponseStatusCode.SUCCESS;
    public final ResponseStatusCode FAILURE = ResponseStatusCode.FAILURE;

    void order(Request request);

    Response query(Request request);

    void run();

}
