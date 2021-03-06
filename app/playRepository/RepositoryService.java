package playRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.tigris.subversion.javahl.ClientException;

import models.Project;

import controllers.GitApp;
import controllers.SvnApp;

public class RepositoryService {
    public static final String VCS_SUBVERSION = "Subversion";
    public static final String VCS_GIT = "GIT";

    public static Map<String, String> vcsTypes() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(VCS_GIT, "project.new.vcsType.git");
        map.put(VCS_SUBVERSION, "project.new.vcsType.subversion");
        return map;
    }

    public static void deleteRepository(String userName, String projectName, String type)
            throws IOException, ServletException {
        if (type.equals(RepositoryService.VCS_GIT)) {
            GitApp.deleteRepository(userName, projectName);
        } else if (type.equals(RepositoryService.VCS_SUBVERSION)) {
            SvnApp.deleteRepository(userName, projectName);
        } else {
            throw new UnsupportedOperationException("only support git & svn!");
        }
    }

    public static void createRepository(Project project) throws IOException, ServletException,
            UnsupportedOperationException, ClientException {
        RepositoryService.deleteRepository(project.owner, project.name, project.vcs);
        if (project.vcs.equals(RepositoryService.VCS_GIT)) {
            GitApp.createRepository(project.owner, project.name);
        } else if (project.vcs.equals(RepositoryService.VCS_SUBVERSION)) {
            SvnApp.createRepository(project.owner, project.name);
        } else {
            throw new UnsupportedOperationException("only support git & svn!");
        }
    }
}