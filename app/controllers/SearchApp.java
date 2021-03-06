package controllers;

import com.avaje.ebean.*;
import models.*;
import play.mvc.*;
import views.html.search.*;

public class SearchApp extends Controller {

    public static class ContentSearchCondition {
        public String filter;
        public int page;
        public int pageSize;
        public String type;

        public ContentSearchCondition() {
            this.filter = "";
            this.page = 1;
            this.pageSize = 10;
            this.type = "all";
        }
    }

    public static Result contentsSearch(String userName, String projectName) {
        Project project = ProjectApp.getProject(userName, projectName);

        if (project == null) {
            notFound();
        }
		/* @TODO 쿼리에 대해서 특수문자나 공백 체크 해야함. */
        ContentSearchCondition condition = form(ContentSearchCondition.class).bindFromRequest().get();

        Page<Issue> resultIssues = null;
        Page<Post> resultPosts = null;

        if(!condition.type.equals("post")) {
            resultIssues = Issue.find(project, condition);
        }
        if(!condition.type.equals("issue")) {
            resultPosts = Post.find(project, condition);
        }

        if(condition.type.equals("post")) {
            return ok(postContentsSearch.render(project, resultPosts));
        } else if(condition.type.equals("issue")) {
            return ok(issueContentsSearch.render(project, resultIssues));
        }

        return ok(contentsSearch.render("title.contentSearchResult", project, condition.filter, resultIssues, resultPosts));
    }
}