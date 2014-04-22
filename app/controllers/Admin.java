package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.DynamicForm;
import views.html.*;
import models.*;
import views.html.admin.*;
import java.util.List;
import java.util.ArrayList;
import models.brownPeterson.*;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.Exception;



public class Admin extends Controller {
    private static final Form<ExperimentSchedule> expForm = Form.form(ExperimentSchedule.class);

    //แสดงผลหน้า home ของ admin
    @Security.Authenticated(Secured.class)
    public static Result index() {
        User user = User.find.where().eq("username", session().get("username")).findUnique();
        List<User> userList = User.find.all();

        if(user == null){
            return redirect(routes.Application.index());
        }

        if(user.status != UserRole.ADMIN) {
            return redirect(routes.Application.home());
        }
        return ok(index_admin.render(User.getAllUser()));
        
    }
    //แสดงผลหน้าข้อมูลของ user ทั้งหมด
    @Security.Authenticated(Secured.class)
    public static Result renderUserInfo() {
       // List<User> userList = User.find.all(); // I think it useless : who wrote it plz take care .
        return ok(user_info.render(User.getAllUser()));
    }

    //ทำการเพิ่ม user account ใหม่เข้าไปในระบบ
    @Security.Authenticated(Secured.class)
    public static Result saveUser() {
        DynamicForm  stringForm = Form.form().bindFromRequest();
        String userString = stringForm.get("users");

        if(userString.contains(" "))return TODO;
        String[] result = userString.split("\r\n");

        boolean userExist = false;
        List<String> userExistName = new ArrayList<String>();
        for(int i=0 ; i <result.length;i++){
            List<User> users = User.find.where().eq("username",result[i]).findList();
            if (users.size() == 0){
                User temp = new User(result[i],result[i]);
                temp.section = stringForm.get("section");
                temp.semester = stringForm.get("semester");
                temp.academicYear = stringForm.get("academicYear");
                temp.department = stringForm.get("department");
                temp.faculty = stringForm.get("faculty");
                temp.save();
            }
            else{
                userExist = true;
                userExistName.add(result[i]);
            }
        }
        if (userExist){
            String errorText = "Already has user: ";
            for (String i : userExistName)
                errorText = errorText + i + " ";
            flash("userExisted", errorText);
        }
        else
            flash("savedSuccess","Add Success");

        return ok(user_info.render(User.getAllUser()));
    }
    
    //แสดงผลหน้า experiment set ทั้งหมดในระบบ
    @Security.Authenticated(Secured.class)
    public static Result displayExperimentList() {
        List<ExperimentSchedule> expList = ExperimentSchedule.find.all();
        return ok(views.html.admin.experiment.main.render(expList));
    }

    //แสดงหน้าเพิ่มชุดการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result addExperiment() {
        return ok(views.html.admin.experiment.add.render(expForm));
    }

    //
    @Security.Authenticated(Secured.class)
    public static Result saveExperiment() {
        Form<ExperimentSchedule> boundForm = expForm.bindFromRequest();

        if(boundForm.hasErrors()){
            flash("error", "please correct the form above.");
            return badRequest(views.html.admin.experiment.add.render(expForm));
        }

        ExperimentSchedule exp = boundForm.get();
        exp.save();
        flash("success","Successfully");
        exp.generateTrials();
        return redirect(routes.Admin.displayExperimentList());
    }

    @Security.Authenticated(Secured.class)
    public static Result displayParameter(long id){
        final ExperimentSchedule exp = ExperimentSchedule.find.byId(id);
        if(exp == null){
            return notFound("This Experiment does not exist.");
        }
        return ok(views.html.admin.experiment.edit.render(exp));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveBrownPetersonParameter(long expId){
        
        ExperimentSchedule exp = ExperimentSchedule.find.byId(expId);
        DynamicForm requestData = Form.form().bindFromRequest();
        
        exp.name = requestData.get("name");
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            exp.startDate = dateFormat.parse(requestData.get("startDate"));
            exp.expireDate = dateFormat.parse(requestData.get("expireDate"));
        } catch (ParseException e){
            flash("date_error", "กรุณากรอกข้อมูลช่วงเวลาการทำทดลองให้ถูกต้อง");
            return badRequest(views.html.admin.experiment.edit.render(exp));
        }
        exp.update();
        /* Create Trials for Experiment */

        List<models.brownPeterson.Trial> trials = models.brownPeterson.Trial.findInvolving(exp);
        for(models.brownPeterson.Trial trial : trials){
            for(models.brownPeterson.Quiz quiz : models.brownPeterson.Quiz.findInvolving(trial)){
                quiz.initCountdown = Integer.parseInt(requestData.get("initCountdown_" + quiz.id));
                quiz.flashTime = Integer.parseInt(requestData.get("flashTime_" + quiz.id));
                quiz.update();
            }
            String trigramType = requestData.get("trigramType-" + trial.id);
            String trigramLanguage = requestData.get("trigramLanguage-" + trial.id);
            if(!(trigramType.equals(trial.trigramType) && trigramLanguage.equals(trial.trigramLanguage))) {
                trial.trigramType = trigramType;
                trial.trigramLanguage = trigramLanguage;
                trial.randomNewQuestions();
            }
            
            trial.update();
        }
        // end create Trial
        flash("success", "update success.");
        return ok(views.html.admin.experiment.edit.render(exp));
    }

    public static Result displayBrownPetersonQuestionList(){
        return ok(views.html.admin.experiment.displayQuestions.render());
    }

    public static Result addBrownPetersonQuestion(){
        return ok(views.html.admin.experiment.addQuestion.render());
    }

    public static Result saveBrownPetersonQuestion(){
        DynamicForm  questionForm = Form.form().bindFromRequest();
        String tempQuestion = questionForm.get("questions");
        String trigramType = questionForm.get("trigramType");
        String trigramLanguage = questionForm.get("trigramLanguage");

        String[] questions = tempQuestion.split("\\r?\\n");
        int counter = 0;

        for(String question : questions){
            try{
                String[] words = null;
                if(question.contains(",")){
                    words = question.split(",");
                }else{
                    words = question.split("\\s+");
                }   
                models.brownPeterson.Question.create(words[0],words[1],words[2], trigramType, trigramLanguage).save();
            }catch(Exception e){
                String warning = "เพิ่มคำถามสำเร็จ " + counter + " คำถาม" +
                                " พบปัญหาที่บรรทัดที่ " + (counter + 1) +
                                " [" + questions[counter] + "]" +
                                " โปรดลองใหม่อีกครั้ง";
                flash("error", warning);
                return badRequest(views.html.admin.experiment.addQuestion.render());
            }
            counter++;
        }

        flash("success", "update success.");
        
        return ok(views.html.admin.experiment.displayQuestions.render());
    }

    public static Result randomBrownPetersonQuestion(long expId, long quizId){
        models.brownPeterson.Quiz quiz = models.brownPeterson.Quiz.find.byId(quizId);
        quiz.randomToNewQuestion();
        flash("success", "เปลี่ยนแปลงคำถามเรียบร้อยแล้ว");
        return redirect(routes.Admin.displayParameter(expId));
    }
    public static Result deleteBrownPetersonQuestion(long questionId){
        models.brownPeterson.Question question = models.brownPeterson.Question.find.byId(questionId);
        if(question.quizzes.size() > 0){
            flash("error", "ไม่สามารถลบได้ เนื่องจากถูกใช้งานอยู่");
            System.out.println(question.quizzes.size());
        }else{
            question.delete();
            flash("success", "ลบคำถามเรียบร้อยแล้ว");
        }
        return redirect(routes.Admin.displayBrownPetersonQuestionList());
    }

    public static Result displayExperimentResult(long expId){
        return ok(views.html.admin.experiment.result.render(ExperimentSchedule.find.byId(expId)));
    }

    public static Result switchStroopEffectQuestion(long expId, long quizId){
        models.stroopEffect.Quiz quiz = models.stroopEffect.Quiz.find.byId(quizId);
        quiz.switchRandomQuestion();
        flash("success", "เปลี่ยนแปลงคำถามเรียบร้อยแล้ว");
        return redirect(routes.Admin.displayParameter(expId));
    }

    public static Result saveStroopEffectParameter(long expId){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(expId);
        DynamicForm  requestData = Form.form().bindFromRequest();

        exp.name = requestData.get("name");
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            exp.startDate = dateFormat.parse(requestData.get("startDate"));
            exp.expireDate = dateFormat.parse(requestData.get("expireDate"));
        } catch (ParseException e){
            flash("date_error", "กรุณากรอกข้อมูลช่วงเวลาการทำทดลองให้ถูกต้อง");
            return badRequest(views.html.admin.experiment.edit.render(exp));
        }
        exp.update();
        
        List<models.stroopEffect.Trial> trials = models.stroopEffect.Trial.findInvolving(exp);
        for(models.stroopEffect.Trial trial : trials ){
            String questionType = requestData.get("questionType-" + trial.id);
            if(!trial.questionType.toString().equals(questionType)){
                trial.setQuestionType(questionType);
                trial.randomNewQuestions();
            }

            for(models.stroopEffect.Quiz quiz : trial.quizzes){
                String isMatchString = requestData.get("isMatch-" + quiz.id);
                boolean isMatch = true;
                if(isMatchString == null){
                    isMatch = false;
                }else{
                    isMatch = true;
                }

                if(isMatch != quiz.question.isMatch()){
                    quiz.switchRandomQuestion();
                }
            }
        }

        flash("success", "update success.");
        return ok(views.html.admin.experiment.edit.render(exp));
    }

    public static Result saveAttentionBlinkParameter(long expId){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(expId);
        DynamicForm  requestData = Form.form().bindFromRequest();

        exp.name = requestData.get("name");
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            exp.startDate = dateFormat.parse(requestData.get("startDate"));
            exp.expireDate = dateFormat.parse(requestData.get("expireDate"));
        } catch (ParseException e){
            flash("date_error", "กรุณากรอกข้อมูลช่วงเวลาการทำทดลองให้ถูกต้อง");
            return badRequest(views.html.admin.experiment.edit.render(exp));
        }
        exp.update();

        List<models.attentionBlink.Trial> trials = models.attentionBlink.Trial.findInvolving(exp);
        
        for(models.attentionBlink.Trial trial : trials){
            boolean isQuestionChange = false;
            String questionType = requestData.get("questionType_" + trial.id);
            if(!trial.questionType.toString().equals(questionType)){
                trial.changeQuestionType(questionType);
                isQuestionChange = true;
                trial.update();
            }

            for(models.attentionBlink.Quiz quiz : trial.quizzes){
                boolean isCorrectChange = false;
                String lengthStr = requestData.get("length_" + quiz.id);
                String numberOfTargetStr = requestData.get("numberOfTarget_" + quiz.id);
                String isCorrectStr = requestData.get("isCorrect_" + quiz.id);
                String blinkTimeStr = requestData.get("blinkTime_" + quiz.id);
                boolean isCorrect = true;
                if(isCorrectStr == null){
                    isCorrect = false;
                }else{
                    isCorrect = true;
                }

                if(isCorrect != quiz.isCorrect){
                    quiz.isCorrect = isCorrect;
                    quiz.question.correctAnswer = isCorrect;
                    isCorrectChange = true;
                }


                    
                try{
                    int length = Integer.parseInt(lengthStr);
                    int numberOfTarget = Integer.parseInt(numberOfTargetStr);
                    double blinkTime = Double.parseDouble(blinkTimeStr);
                    if(length != quiz.length){
                        quiz.length = length;
                        isQuestionChange = true;
                    }
                    if(numberOfTarget != quiz.numberOfTarget){
                        quiz.numberOfTarget = numberOfTarget;
                        isQuestionChange = true;
                    }
                    quiz.blinkTime = blinkTime;
                } catch(Exception e){
                    flash("error", "ข้อมูลไม่ถูกต้อง โปรดตรวจสอบอีกครั้ง");
                    return badRequest(views.html.admin.experiment.edit.render(exp));
                }
                if(isCorrectChange && !isQuestionChange){
                    quiz.question.changeQuestionSetByCorrectAnswer();
                }
                quiz.update();
            }

            if(isQuestionChange){
                trial.generateNewQuestions();
                models.attentionBlink.Question.deleteAllUnusedQuestion();
            }
        }

        flash("success", "update success.");
        return ok(views.html.admin.experiment.edit.render(exp));
    }

    public static Result saveSignalDetectionParameter(long expId){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(expId);
        DynamicForm  requestData = Form.form().bindFromRequest();

        exp.name = requestData.get("name");
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            exp.startDate = dateFormat.parse(requestData.get("startDate"));
            exp.expireDate = dateFormat.parse(requestData.get("expireDate"));
        } catch (ParseException e){
            flash("date_error", "กรุณากรอกข้อมูลช่วงเวลาการทำทดลองให้ถูกต้อง");
            return badRequest(views.html.admin.experiment.edit.render(exp));
        }
        exp.update();

        List<models.signalDetection.Trial> trials = models.signalDetection.Trial.findInvolving(exp);

        for(models.signalDetection.Trial trial : trials){
            for(models.signalDetection.Quiz quiz : trial.quizzes){
                
                String targetString = requestData.get("target-" + quiz.id);
                String noiseString = requestData.get("noise-" + quiz.id);
                String lengthString = requestData.get("length-" + quiz.id);
                String noOfTargetString = requestData.get("noOfTarget-" + quiz.id);
                String displayTimeString = requestData.get("displayTime-" + quiz.id);

                char target = targetString.charAt(0);
                char noise = noiseString.charAt(0);
                int length = Integer.parseInt(lengthString);
                int noOfTarget = Integer.parseInt(noOfTargetString);
                double displayTime = Double.parseDouble(displayTimeString);
                quiz.question.target = target;
                quiz.question.noise = noise;
                quiz.length = length;
                quiz.noOfTarget = noOfTarget;
                quiz.displayTime = displayTime;
                quiz.question.update();
                quiz.update();
                
            }
        }

        flash("success", "update success.");
        return ok(views.html.admin.experiment.edit.render(exp));
    }
}
