package models.brownPeterson;

import models.*;

import play.db.ebean.*;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Trial extends Model{
	@Id
	public long id;
	@Column(nullable=false, length=20)
	public String trigramType = "word";
	@Column(nullable=false, length=20)
	public String trigramLanguage = "english";

	public static final int TOTAL_QUESTION = 3;

	@ManyToOne
	public ExperimentSchedule schedule;
	@OneToMany
	public List<TimeLog> timeLogs = new ArrayList<TimeLog>();
    @OneToMany(mappedBy = "trial")
    public List<Quiz> quizzes = new ArrayList<Quiz>();
        
	public static Trial create(ExperimentSchedule experimentSchedule){
		Trial trial = new Trial();
		trial.schedule = experimentSchedule;
		return trial;
	}

	public static Trial findById(int id) {
		return find.byId(new Long(id));
	}

	public static List<Trial> findInvolving(ExperimentSchedule ex){
		return find.where().eq("schedule", ex).findList();
	}
/*
	public static boolean inWorkingSchedule(Trial trial) {
		List<ExperimentSchedule> exps = ExperimentSchedule.getAllWorkingExperiments();
		for(ExperimentSchedule exp : exps) {
			List<Trial> trials = Trial.find.where().eq("schedule_id", exp.id).findList();
			return trials.contains(trial);
		}
		return false;
	}
*/
	

	@SuppressWarnings("unchecked")
	public static Finder<Long, Trial> find = new Finder(Long.class, Trial.class);

}
