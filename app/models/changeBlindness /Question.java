package models.changeBlindness;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name="change_blindness_question")
public class Question extends Model{
    @Id
    public long id;
    public String pathOfPic1;
    public String pathOfPic2;
    public double answerAreaWidth;
    public double answerAreaHeight;
    public double positionOfChangeX;
    public double positionOfChangeY;
    @OneToMany
    public List<Quiz> quizzes = new ArrayList<Quiz>();

    public Question(){
        this.pathOfPic1 = "";
        this.pathOfPic2 = "";
        this.positionOfChangeX = 0;
        this.positionOfChangeY = 0;
        this.answerAreaWidth = 0;
        this.answerAreaHeight = 0;
    }

    public static Question create(String pathOfPic1, String pathOfPic2, double answerAreaWidth, 
        double answerAreaHeight, double positionOfChangeX, double positionOfChangeY){
        Question newQuestion = new Question();
        newQuestion.pathOfPic1 = pathOfPic1;
        newQuestion.pathOfPic2 = pathOfPic2;
        newQuestion.answerAreaWidth = answerAreaWidth;
        newQuestion.answerAreaHeight = answerAreaHeight;
        newQuestion.positionOfChangeX = positionOfChangeX;
        newQuestion.positionOfChangeY = positionOfChangeY;
        return newQuestion;
    }

    @SuppressWarnings("unchecked")
    public static Finder<Long, Question> find = new Finder(Long.class, Question.class);

}