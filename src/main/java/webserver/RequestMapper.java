package webserver;

import service.UserService;

public class RequestMapper {
    public HttpResponse processRequest(HttpRequest request) {
        UserService service = new UserService();
        if (isUserCreate(request)) {
            service.createUser(request);
            HttpResponse response = service.performHttpRedirect(request);
            return response;
        }
        return null;
    }

    private static boolean isUserCreate(HttpRequest request) {
        return "/user/create".equals(request.getRequestUri());
    }

}
