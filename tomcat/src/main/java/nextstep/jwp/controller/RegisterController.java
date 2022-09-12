package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParameters;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.FileReader;

public class RegisterController extends AbstractController {

    private static final List<String> PATHS = List.of("/register");

    @Override
    public boolean containsPath(final String path) {
        return PATHS.contains(path);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestUri requestUri = httpRequest.getRequestUri();
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseStaticFilePath()), requestUri.findMediaType());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestParameters requestParameters = httpRequest.getRequestParameters();
        register(httpResponse, requestParameters);
    }

    private void register(final HttpResponse httpResponse, final RequestParameters requestParameters) {
        User user = new User(
                requestParameters.get("account"),
                requestParameters.get("password"),
                requestParameters.get("email")
        );
        InMemoryUserRepository.save(user);
        httpResponse.redirect("/index.html");
    }
}
