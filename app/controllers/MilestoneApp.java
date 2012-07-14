package controllers;

import models.Milestone;
import models.enumeration.MilestoneState;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.milestone.list;

import java.util.List;

public class MilestoneApp extends Controller {

    public static Result milestones(Long projectId, String state) {
        List<Milestone> milestones = Milestone.findMilestones(projectId, MilestoneState.getValue(state));
        return ok(list.render("마일스톤 리스트", milestones, projectId, state));
    }

    public static Result newMilestone(Long projectId) {
        return TODO;
        /*
          Form<Milestone> filledForm = milestoneForm.bindFromRequest();
          if(filledForm.hasErrors()){
              return badRequest(
                      index.render(Milestone.all(), filledForm)
                      );
          }else{
              Milestone.create(filledForm.get());
              return redirect(routes.MilestoneApp.milestoneList());
          }
          */
    }

    public static Result createMilestone() {
        return TODO;
    }

    public static Result updateMilestone(Long id) {
        return TODO;
    }

    public static Result deleteMilestone(Long id) {
        return TODO;
    }
}
