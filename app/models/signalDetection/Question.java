package models.signalDetection;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name="signal_detection_question")
public class Question extends Model{
    @Id
    public long id;
    public char target;
    public char noise;

    @OneToMany
    public List<Quiz> quizzes = new ArrayList<Quiz>();

    public Question(){}

    public Question(char target,char noise){
        this.target = target;
        this.noise = noise;
    }

    @SuppressWarnings("unchecked")
    public static Finder<Long, Question> find = new Finder(Long.class,Question.class);

}
