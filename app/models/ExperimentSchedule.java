package models;
import models.brownPeterson.Trial;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.Date;
import play.data.format.Formats;
import java.util.List;
import com.avaje.ebean.*;
import java.util.ArrayList;
import static play.data.validation.Constraints.*;


@Entity
public class ExperimentSchedule extends Model{
	@Id
	public long id;
	@Required
	@Column(nullable=false, length=30)
	public String name = "Blank Experiment";
	@Column(nullable=false, length=2)
	public int noOfTrial = 3;
	@Column(nullable=false)
	@Required
	@Formats.DateTime(pattern="dd/mm/yyyy")
	public Date startDate;
	@Required
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/mm/yyyy")
	public Date expireDate;
	public ExperimentType experimentType;
	
	@OneToMany(mappedBy="schedule")
	public List<models.brownPeterson.Trial> trials = new ArrayList<models.brownPeterson.Trial>();
	@OneToMany(mappedBy="schedule")
	public List<models.stroopEffect.Trial> stroopTrials = new ArrayList<models.stroopEffect.Trial>();


	public ExperimentSchedule(String name, int noOfTrial, Date startDate, Date expireDate, ExperimentType experimentType) {
		this.name = name;
		this.noOfTrial = noOfTrial;
		this.startDate = startDate;
		this.expireDate = expireDate;
		this.experimentType = experimentType;
	}

	public static List<ExperimentSchedule> getAllWorkingExperiments() {
		List<ExperimentSchedule> experiments = find.where().betweenProperties("startDate", "expireDate", new Date()).findList();
		return experiments;
	}

	public static List<ExperimentSchedule> getWorkingExperimentsByType(ExperimentType expType) {
		List<ExperimentSchedule> experiments = getAllWorkingExperiments();
		for(ExperimentSchedule exp : experiments) {
			if(exp.experimentType != expType) {
				experiments.remove(exp);
			}
		}
		return experiments;
	}
	
	public String getExperimentTypeToString(){
		String type = "Unknown";
		if(this.experimentType != null){
			switch(this.experimentType){
				case BROWNPETERSON : type = "Brown Peterson"; break;
				case STROOPEFFECT  : type = "Stroof Effect"; break;
				default : type = "Unknown";
			}
		}
		return type;
	}

	public void generateTrials(){
		switch(this.experimentType){
			case BROWNPETERSON : generateTrialsByNoOfTrial() ;break;
			case STROOPEFFECT : break;
		}
	}

	private void generateTrialsByNoOfTrial(){
		for(int i = 0; i < this.noOfTrial; i++){
			models.brownPeterson.Trial trial = models.brownPeterson.Trial.create(this);
            trial.save();
            List<models.brownPeterson.Question> questions 
            	= models.brownPeterson.Question.getQuestionListBy(models.brownPeterson.Trial.TOTAL_QUESTION); // 3 is number of quiz in trial.
            for(int j = 0; j < models.brownPeterson.Trial.TOTAL_QUESTION; j++){
                models.brownPeterson.Quiz.create(100, 5, trial, questions.get(j)).save();
            }
		}
	}

	@SuppressWarnings("unchecked")
	public static Finder<Long,ExperimentSchedule> find = new Finder(Long.class,ExperimentSchedule.class);
}