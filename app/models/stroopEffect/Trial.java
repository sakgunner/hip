package models.stroopEffect;

import play.db.ebean.Model;
import javax.persistence.*;

import models.TimeLog;

import models.ExperimentSchedule;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table (name="stroop_trial")
public class Trial extends Model{
    @Id
    public long id;
    @Column(nullable=true, length=2)
    public long appearTime = 0L;
    @Column(nullable=true, length=20)
    public QuestionType questionType = QuestionType.ENGLISH;
    public double totalScore = 0;
    public double totalUsedTime = 0;
    public int totalUser = 0;

    public static final int TOTAL_QUESTION = 3;

    @ManyToOne
    @JsonManagedReference
    public ExperimentSchedule schedule;
//    @OneToMany
//    public List<TimeLog> timeLogs = new ArrayList<TimeLog>();
    @OneToMany(cascade=CascadeType.REMOVE)
    @JsonManagedReference
    public List<Quiz> quizzes = new ArrayList<Quiz>();

    public static Trial create(ExperimentSchedule experimentSchedule){
        Trial trial = new Trial();
        trial.schedule = experimentSchedule;
        return trial;
    }

    public Trial(){}

    public Trial(long appearTime,QuestionType type){
        this.appearTime = appearTime;
        this.questionType = type;
    }

    public void updateResult(){
        this.totalScore = 0;
        this.totalUsedTime = 0;
        for(Quiz q:quizzes){
            this.totalScore += Answer.calculateTotalScore(q.answers);
            this.totalUsedTime += Answer.calculateTotalUsedTime(q.answers);
        }
        this.totalUser = TimeLog.calaulateTotalUserTakeExp(schedule,id);
    }
    
    public static Trial findById(long id){
        return find.byId(new Long(id));
    }

    public static List<Trial> findInvolving(ExperimentSchedule ex){
        return find.where().eq("schedule", ex).findList();
    }

    public void toQuestionTYpe(String type){
        if(type.equals(QuestionType.ENGLISH.toString())){
            this.questionType = QuestionType.ENGLISH;
        }else if(type.equals(QuestionType.THAI.toString())){
            this.questionType = QuestionType.THAI;
        }else if(type.equals(QuestionType.SHAPE.toString())){
            this.questionType = QuestionType.SHAPE;
        }
        update();
    }

    public void randomNewQuestions(){
        List<Question> questions = Question.find.where().eq("questionType", questionType).findList();
        for(Quiz quiz : quizzes){
            quiz.randomToNewQuestion(questions);
        }
    }

    public void generateQuiz(){
        List<Question> questions = Question.find.where().eq("questionType", questionType).findList();
        for(int i = 0; i < TOTAL_QUESTION; i++){
            Quiz.create(this, Question.randomNewQuestion(questions)).save();
        }
    }

    @SuppressWarnings("unchecked")
    public static Model.Finder<Long,Trial> find = new Finder(Long.class, Trial.class);
}
