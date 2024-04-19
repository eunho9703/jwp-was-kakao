package utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import webserver.http.request.HttpRequestHeader;
import webserver.http.request.RequestLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequestParserTest {

    @Test
    void http_헤더의_첫_줄에서_method_path_protocol를_추출한다() {
        //given
        //when
        RequestLine requestLine = HttpRequestParser.parseRequestLine("GET /index.html HTTP/1.1");

        //then
        Assertions.assertThat(requestLine.getMethod()).isEqualTo("GET");
        Assertions.assertThat(requestLine.getRequestUri()).isEqualTo("/index.html");
        Assertions.assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void path에서_queryString을_추출한다() {
        //given
        String startLine = "GET /user/create?userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com HTTP/1.1";

        //when
        RequestLine requestLine = HttpRequestParser.parseRequestLine(startLine);

        //then
        Assertions.assertThat(requestLine.getQueryString())
                .isEqualTo("userId=cu&password=password&name=이동규&email=brainbackdoor@gmail.com");
    }

    @Test
    void urlEncodedString에서_query_parameter를_추출한다() {
        //given
        String urlEncodedString = "userId=cu&password=password&name=이동규&email=brainbackdoor@gmail.com";

        //when
        Map<String, String> queryParams = HttpRequestParser.parseQueryParams(urlEncodedString);

        //then
        Assertions.assertThat(queryParams.get("userId")).isEqualTo("cu");
        Assertions.assertThat(queryParams.get("password")).isEqualTo("password");
        Assertions.assertThat(queryParams.get("name")).isEqualTo("이동규");
        Assertions.assertThat(queryParams.get("email")).isEqualTo("brainbackdoor@gmail.com");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "/index.html ; html",
            "/scripts.js ; js",
            "/styles.css ; css",
            "/favicon.ico ; ico",
            "/user/create ; ",
    }, delimiter = ';')
    void parser는_requestUri를_받아_확장자를_알려준다(String requestUri, String expected) {
        //given
        //when
        String actual = HttpRequestParser.parseExt(requestUri);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void parser는_http요청을_받아_헤더를_추출한다() {
        //given
        List<String> headerString = new ArrayList<>();
        headerString.add("Host: localhost:8080");
        headerString.add("Connection: keep-alive");
        headerString.add("Accept: */*");

        //when
        HttpRequestHeader header = HttpRequestParser.parseHeaders(headerString);

        //then
        Assertions.assertThat(header.getHost()).isEqualTo("localhost:8080");
        Assertions.assertThat(header.getConnection()).isEqualTo("keep-alive");
        Assertions.assertThat(header.getAccept()).isEqualTo("*/*");
    }
}
