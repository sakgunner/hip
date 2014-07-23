package models.positionError;

import play.db.ebean.*;
import javax.persistence.*;
import models.TimeLog;
import models.ExperimentSchedule;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name="position_error_trial")
public class Trial extends Model{
	@Id
	public long id;

	public double flashSpeed;
	public double delayTime;
	public QuestionType questionType;

    public int numberOfQuiz = 3;

	@ManyToOne
    public ExperimentSchedule schedule;
    @OneToMany(cascade=CascadeType.REMOVE)
    public List<Quiz> quizzes;

	public Trial(double flashSpeed, double delayTime, QuestionType questionType){
		this.flashSpeed = flashSpeed;
		this.delayTime = delayTime;
		this.questionType = questionType;
	}

	public static Trial create(double flashSpeed, double delayTime, QuestionType questionType, 
								ExperimentSchedule schedule){
		Trial newTrial = new Trial(flashSpeed, delayTime, questionType);
		newTrial.schedule = schedule;
		return newTrial;
	}

    public static Trial create(ExperimentSchedule schedule){
        Trial newTrial = new Trial(0.05,0.1,QuestionType.ENGLISH);
        newTrial.schedule = schedule;
        return newTrial;
    }

    public void generateQuiz(){
        for(int i = 0; i < this.numberOfQuiz; i++){
            Quiz.create(this).save();
        }
    }

    public static List<Trial> findInvolving(ExperimentSchedule ex){
        return find.where().eq("schedule", ex).findList();
    }

	@SuppressWarnings("unchecked")
    public static Model.Finder<Long,Trial> find = new Finder(Long.class, Trial.class);




}
