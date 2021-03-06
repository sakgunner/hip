package models.unitTest;

import models.mullerLayer.*;
import models.ExperimentSchedule;
import models.ExperimentType;
import models.User;
import play.test.WithApplication;
import org.junit.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class MullerLayerUnitTest extends WithApplication {

    @Before
    public void setUp(){
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date lastYearDate = calendar.getTime();
        calendar.add(Calendar.YEAR, +2);
        Date nextYearDate = calendar.getTime();
        new ExperimentSchedule("Experiment 1", 3, lastYearDate,
         		nextYearDate, ExperimentType.MULLERLAYER).save();
        new User("admin","admin").save();
    }

    @Test
    public void createQuestionShouldNotNull(){
    	Question q = new Question();
    	assertNotNull(q);
    }

    @Test
    public void queryQuestionShouldNotNull(){
        new Question().save();
        Question q = Question.find.byId(1L);
        assertNotNull(q);
    }

    @Test
    public void createTrialShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        Trial t = new Trial(exp);
        assertNotNull(t);
        assertEquals(exp, t.schedule);
    }

    @Test
    public void queryTrialShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        assertNotNull(t);
        assertEquals(exp, t.schedule);
    }

    @Test
    public void createQuizShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        Trial t = new Trial(exp);
        Question q = new Question();
        Quiz quiz = new Quiz(t, q);
        assertNotNull(quiz);
        assertEquals(t, quiz.trial);
        assertEquals(q, quiz.question);
    }

    @Test
    public void queryQuizShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        Question q = Question.find.byId(1L);
        new Quiz(t, q).save();
        Quiz quiz = Quiz.find.byId(1L);
        assertNotNull(quiz);
        assertEquals(t, quiz.trial);
        assertEquals(q, quiz.question);
    }

    @Test
    public void createAnswerShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        Trial t = new Trial(exp);
        Question q = new Question();
        Quiz quiz = new Quiz(t, q);
        User user = User.find.byId("admin");
        Answer ans = new Answer(user, quiz);
        assertNotNull(ans);
        assertEquals(user, ans.user);
        assertEquals(quiz, ans.quiz);
    }

    @Test
    public void queryAnswerShouldNotNull(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        Question q = Question.find.byId(1L);
        new Quiz(t, q).save();
        Quiz quiz = Quiz.find.byId(1L);
        User user = User.find.byId("admin");
        new Answer(user, quiz).save();
        Answer ans = Answer.find.byId(1L);
        assertNotNull(ans);
        assertEquals(user, ans.user);
        assertEquals(quiz, ans.quiz);
    }

    @Test
    public void createAnswerShouldHaveParameter(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        Question q = Question.find.byId(1L);
        new Quiz(t, q).save();
        Quiz quiz = Quiz.find.byId(1L);
        User user = User.find.byId("admin");
        Answer ans = Answer.create(user, quiz, 3, 0.75, true);
        assertNotNull(ans);
        assertEquals(user, ans.user);
        assertEquals(quiz, ans.quiz);
        assertEquals(3, ans.answer);
        assertEquals(0.75, ans.usedTime, 0.001);
        assertTrue(ans.isCorrect);
    }

    @Test
    public void queryAnswerWithParameterShouldCorrect(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        Question q = Question.find.byId(1L);
        new Quiz(t, q).save();
        Quiz quiz = Quiz.find.byId(1L);
        User user = User.find.byId("admin");
        Answer.create(user, quiz, 3, 0.75, true).save();
        Answer ans = Answer.find.byId(1L);
        assertNotNull(ans);
        assertEquals(user, ans.user);
        assertEquals(quiz, ans.quiz);
        assertEquals(3, ans.answer);
        assertEquals(0.75, ans.usedTime, 0.001);
        assertTrue(ans.isCorrect);
    }

    @Test
    public void getListAnswerFromUserAndTrialShouldCorrect(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        new Question().save();
        new Question().save();
        Question q1 = Question.find.byId(1L);
        Question q2 = Question.find.byId(2L);
        Question q3 = Question.find.byId(3L);
        new Quiz(t, q1).save();
        new Quiz(t, q2).save();
        new Quiz(t, q3).save();
        Quiz quiz1 = Quiz.find.byId(1L);
        Quiz quiz2 = Quiz.find.byId(2L);
        Quiz quiz3 = Quiz.find.byId(3L);
        User user = User.find.byId("admin");
        Answer.create(user, quiz1, 3, 0.75, true).save();
        Answer.create(user, quiz2, 1, 0.70, true).save();
        Answer.create(user, quiz3, 2, 0.55, false).save();
        List<Answer> answers = new ArrayList<Answer>();
        answers = Answer.findInvolving(user, t.quizzes);
        assertNotNull(answers);
        assertEquals(Answer.find.byId(1L), answers.get(0));
        assertEquals(Answer.find.byId(2L), answers.get(1));
        assertEquals(Answer.find.byId(3L), answers.get(2));
    }

    @Test
    public void calculateScoreAndUsedTimeShouldCorrect(){
        ExperimentSchedule exp = ExperimentSchedule.find.byId(1L);
        new Trial(exp).save();
        Trial t = Trial.find.byId(1L);
        new Question().save();
        new Question().save();
        new Question().save();
        Question q1 = Question.find.byId(1L);
        Question q2 = Question.find.byId(2L);
        Question q3 = Question.find.byId(3L);
        new Quiz(t, q1).save();
        new Quiz(t, q2).save();
        new Quiz(t, q3).save();
        Quiz quiz1 = Quiz.find.byId(1L);
        Quiz quiz2 = Quiz.find.byId(2L);
        Quiz quiz3 = Quiz.find.byId(3L);
        User user = User.find.byId("admin");
        Answer.create(user, quiz1, 3, 0.75, true).save();
        Answer.create(user, quiz2, 1, 0.70, true).save();
        Answer.create(user, quiz3, 2, 0.55, false).save();
        List<Answer> answers = new ArrayList<Answer>();
        answers = Answer.findInvolving(user, t.quizzes);
        assertEquals(2, Answer.calculateTotalScore(answers));
        assertEquals(2.00, Answer.calculateTotalUsedTime(answers), 0.001);
    }
}