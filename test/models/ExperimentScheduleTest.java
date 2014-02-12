import org.junit.*;
import java.util.Date;
import java.util.Calendar;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import models.brownPeterson.*;
import java.util.List;
import models.*;

public class ExperimentScheduleTest extends WithApplication {
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
	}

	@Test
	public void retrieveAllWorkingExperiment() {
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, nextYearDate, ExperimentType.BROWNPETERSON).save();
		
		assertEquals(1, ExperimentSchedule.getAllWorkingExperiments().size());
	}

	@Test
	public void retrieveAllWorkingExperimentShouldNotNull() {
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, nextYearDate, ExperimentType.BROWNPETERSON).save();
		
		assertNotNull(ExperimentSchedule.getAllWorkingExperiments());
	}

	@Test
	public void shouldBeCreateCorrectlyHasNoOfTrial() {
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, nextYearDate, ExperimentType.BROWNPETERSON).save();
		
		ExperimentSchedule experimentSchedule = ExperimentSchedule.find.byId(1L);

		assertEquals("Experiment 1", experimentSchedule.name);
		assertEquals(5, experimentSchedule.noOfTrial);
		assertEquals(lastYearDate, experimentSchedule.startDate);
		assertEquals(nextYearDate, experimentSchedule.expireDate);
		assertEquals(ExperimentType.BROWNPETERSON, experimentSchedule.experimentType);
	}

	@Test
	public void shouldBeNotNull() {
		assertNotNull(new ExperimentSchedule());
	}

	@Test
	public void getAllWorkingExperimentsByExperimentType() {
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, nextYearDate, ExperimentType.BROWNPETERSON).save();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, lastYearDate, ExperimentType.BROWNPETERSON).save();
		new ExperimentSchedule("Experiment 1", 5, nextYearDate, nextYearDate, ExperimentType.BROWNPETERSON).save();
		assertEquals(1, ExperimentSchedule.getWorkingExperimentsByType(ExperimentType.BROWNPETERSON).size());
	}

	@Test
	public void experiment_should_generate_new_brownPeterson_trials_by_noOfTrial(){
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, 
			nextYearDate, ExperimentType.BROWNPETERSON).save();
		
		for(int i = 0; i < 20;i++){
			new Question("w1","w2","w3").save();
		}

		ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
		exp.generateTrials();
		assertEquals(5, exp.trials.size());

	}

	@Test
	public void experiment_should_generate_new_brownPeterson_quiz_in_the_trial_by_total_question(){
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
		new ExperimentSchedule("Experiment 1", 5, lastYearDate, 
			nextYearDate, ExperimentType.BROWNPETERSON).save();

		for(int i = 0; i < 20;i++){
			new Question("w1","w2","w3").save();
		}
		
		ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
		exp.generateTrials();
		assertEquals(3, exp.trials.get(0).quizzes.size());
	}
}