package controllers;

import models.brownPeterson.*;
import models.*;
import play.*;
import play.mvc.*;
import play.data.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import views.html.brownPeterson.*;
import views.html.iframe.*;
import java.util.Date;

public class BrownPeterson extends Controller {
    public static List<Answer> answerList = new ArrayList<Answer>();
    public static List<Question> questions = null;
    private static List<ExperimentSchedule> currentEx = ExperimentSchedule.getAllWorkingExperiments();
    public static int questionNumber = 0;

    @Security.Authenticated(Secured.class)
    public static Result info(){
        User user = User.find.byId(request().username());
        return ok(info.render(user));
    }

    @Security.Authenticated(Secured.class)
    public static Result infoIframe(){
        return ok(brown_peterson_iframe.render());
    }
    
    @Security.Authenticated(Secured.class)
    public static Result proc(){
        User user = User.find.byId(request().username());
        return ok(proc.render(user));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result procIframe(){
        return ok(brown_peterson_proc_iframe.render());
    }
    
    @Security.Authenticated(Secured.class)
    public static Result experiment(long trialId){
        User user = User.find.where().eq("username", session().get("username")).findUnique();
        Trial trial = Trial.find.where().eq("id", trialId).findUnique();
        List<Quiz> quizzes = Quiz.find.where().eq("trial_id", trialId).findList();
        questions = Question.findInvolving(quizzes);
        Form<Answer> filledForm = Form.form(Answer.class);
        Answer answer = filledForm.bindFromRequest().get();
        if (questionNumber > 0){
            answerList.add(answer);
        }
        if (questionNumber+1 <= questions.size()){
            int flashTime = quizzes.get(questionNumber).flashTime * 1000;
            return ok(exp.render(questions.get(questionNumber),quizzes.get(questionNumber++).initCountdown, flashTime,trialId));
        }
        else{
            List<Answer> answerListTemp = new ArrayList<Answer>(answerList);
            for(int i = 0; i < answerListTemp.size(); i++){
                answerListTemp.get(i).user = user;
                answerListTemp.get(i).quiz = quizzes.get(i);
                answerListTemp.get(i).save();
            }
            questionNumber = 0;
            answerList.clear();
            return redirect(routes.BrownPeterson.report(user.username, trialId));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result report(String username, Long trialId){
        if(username.equals("") || trialId == 0){
            return redirect(controllers.routes.BrownPeterson.info());
        }
        User user = User.find.where().eq("username", username).findUnique();
        Trial trial = Trial.find.where().eq("id", trialId).findUnique();
        List<Quiz> quizzes = Quiz.findInvolving(trial);
        List<Question> questions = Question.findInvolving(quizzes);
        List<Answer> answers = Answer.findInvolving(user, quizzes);

        double totalUsedTime = Answer.calculateTotalUsedTime(answers);  
        int score = Answer.calculateTotalScore(answers);
        return ok(report.render(score,totalUsedTime,quizzes.size(), "Report", user));
    }

    @Security.Authenticated(Secured.class)
    public static Result checkUserTakeRepeatExperiment() {
        User user = User.find.where().eq("username", session().get("username")).findUnique();
        if(user == null) {
            return redirect(routes.Application.index());
        }
        if(TimeLog.isRepeatTrial(user, Trial.find.byId(new Long(1)))) {
            flash("repeat", "คุณเคยทำการทดลองนี้แล้ว หากต้องการทำต่อโปรดติดต่อผู้ดูแลระบบ");
            return ok(proc.render(user));
        }
        TimeLog.create(new Date(), user, Trial.find.byId(new Long(1))).save();
        questionNumber = 0;
        return redirect(routes.BrownPeterson.experiment(new Long(1)));
    }
}
