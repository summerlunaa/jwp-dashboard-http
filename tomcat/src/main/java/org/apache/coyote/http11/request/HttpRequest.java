package org.apache.coyote.http11.request;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestParameters requestParameters;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestParameters = RequestParameters.of(requestBody);
    }

    public Session getSession(final boolean create) {
        String jSessionId = headers.getJSessionId();
        if (jSessionId != null && !create) {
            return SessionManager.findSession(jSessionId);
        }
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        return session;
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public RequestParameters getRequestParameters() {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return requestLine.getRequestUri().getRequestParameters();
        }
        return requestParameters;
    }
}
