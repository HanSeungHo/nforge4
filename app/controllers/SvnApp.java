package controllers;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;

import models.Project;
import models.enumeration.Operation;
import models.enumeration.Resource;

import org.apache.commons.lang.StringUtils;
import org.tigris.subversion.javahl.ClientException;
import org.tmatesoft.svn.core.internal.server.dav.DAVServlet;
import org.tmatesoft.svn.core.internal.server.dav.handlers.DAVHandlerFactory;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import playRepository.SVNRepository;
import utils.AccessControl;
import utils.BasicAuthAction;
import utils.Config;
import utils.PlayServletContext;
import utils.PlayServletRequest;
import utils.PlayServletResponse;
import utils.PlayServletSession;

public class SvnApp extends Controller {
    static DAVServlet davServlet;
    public static final String REPO_PREFIX = "repo/svn/";

    @With(BasicAuthAction.class)
    @BodyParser.Of(BodyParser.Raw.class)
    public static Result serviceWithPath(String path) throws ServletException, IOException {
        return service();
    }

    @With(BasicAuthAction.class)
    @BodyParser.Of(BodyParser.Raw.class)
    public static Result service() throws ServletException, IOException {
        // FIXME DAVServlet 들어내고 싶다.
        String path;
        try {
            path = new java.net.URI(request().uri()).getPath();
        } catch (URISyntaxException e) {
            return badRequest();
        }

        // If the url starts with slash, remove the slash.
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Split the url into three segments: "svn", userName, pathInfo
        String[] segments = path.split("/", 3);
        if (segments.length < 3) {
            return forbidden();
        }

        // Get userName and pathInfo from path segments.
        String userName = segments[1];
        String pathInfo = segments[2];

        // Get projectName from the pathInfo.
        String projectName = pathInfo.split("/", 2)[0];

        // Check the user has a permission to access this repository.
        Project project = Project.findByNameAndOwner(userName, projectName);
        if (!AccessControl.isAllowed(session().get(UserApp.SESSION_USERID), project.id,
                Resource.CODE, getRequestedOperation(request().method()), null)) {
            return forbidden("You have no permission to access this repository.");
        }

        // Transform request and response in this context to ServletRequest and
        // ServletResponse for DAVServlet.
        PlayServletRequest request = new PlayServletRequest(request(), new PlayServletSession(
                new PlayServletContext()), SVNEncodingUtil.uriEncode(pathInfo));
        PlayServletResponse response = new PlayServletResponse(response());

        // Get DAVServlet from SVNRepository and serve the request using it.
        new SVNRepository(userName, "").getCore().service(request, response);

        response.flushBuffer();

        return status(response.getStatus(), response.getBuffer().toByteArray());
    }

    public static void createRepository(String userName, String projectName) throws ClientException, ServletException {
        new SVNRepository(userName, projectName).create();
    }

    public static String getURL(String ownerName, String projectName) {
        String[] pathSegments = { "svn", ownerName, projectName };
        return Config.getScheme("http") + "://" + Config.getHostport(request().host()) + "/"
                + StringUtils.join(pathSegments, "/");
    }

    public static Result showRawCode(String userName, String projectName, String path) {
        // TODO Auto-generated method stub
        
        return null;
    }

    public static void deleteRepository(String userName, String projectName) throws ServletException {
        new SVNRepository(userName, projectName).delete();
        
    }

    private static Operation getRequestedOperation(String method) {
        if (DAVHandlerFactory.METHOD_OPTIONS.equals(method)
                || DAVHandlerFactory.METHOD_PROPFIND.equals(method)
                || DAVHandlerFactory.METHOD_GET.equals(method)
                || DAVHandlerFactory.METHOD_REPORT.equals(method)) {
            return Operation.READ;
        } else {
            return Operation.WRITE;
        }
    }

}
