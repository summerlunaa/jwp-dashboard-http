package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.controller.ControllerContainer;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final ControllerContainer CONTROLLER_CONTAINER
            = new ControllerContainer(List.of(new HomeController(), new LoginController(), new RegisterController()));

    @Test
    void processor에서_적절한_응답을_만들어_반환한다() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, CONTROLLER_CONTAINER);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index_html_요청시_index_html을_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, CONTROLLER_CONTAINER);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_성공시_302와_setCookie를_반환하고_index_html로_리다이렉트한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, CONTROLLER_CONTAINER);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Set-Cookie: ";

        assertThat(socket.output().startsWith(expected)).isTrue();
    }

    @Test
    void login_실패시_302를_반환하고_401_html로_리다이렉트한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=wrong&password=wrong");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, CONTROLLER_CONTAINER);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "\r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
