package org.apache.coyote.http11.request;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestUri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(final String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");

        return new RequestLine(
                HttpMethod.valueOf(splitRequestLine[0]),
                RequestUri.of(splitRequestLine[1]),
                splitRequestLine[2]
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }
}
