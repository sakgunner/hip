package models.brownPeterson;
import models.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table (name="brown_peterson_answer")
public class Answer extends Model{
	@Id
	public long id;
	@Column(length=20)
	public String firstWord;
	@Column(length=20)
	public String secondWord;
	@Column(length=20)
	public String thirdWord;
	public double usedTime;
	@Column(length=20)
	public String countdownResult;
	public boolean isCorrect;

	@ManyToOne
	public User user;
	@ManyToOne
        @JsonManagedReference
	public Quiz quiz;

	public Answer(String firstWord,String secondWord,String thirdWord,double usedTime,String countdownResult, User user, Quiz quiz){
		this.firstWord = firstWord;
		this.secondWord = secondWord;
		this.thirdWord = thirdWord;
		this.usedTime = usedTime;
		this.countdownResult = countdownResult;
		this.user = user;
		this.quiz = quiz;

		Question q = quiz.question;
		if(!q.firstWord.equalsIgnoreCase(firstWord) && !q.firstWord.equalsIgnoreCase(secondWord) && !q.firstWord.equalsIgnoreCase(thirdWord)){
			this.isCorrect = false;
		}
		else if(!q.secondWord.equalsIgnoreCase(firstWord) && !q.secondWord.equalsIgnoreCase(secondWord) && !q.secondWord.equalsIgnoreCase(thirdWord)){
			this.isCorrect = false;
		}
		else if(!q.thirdWord.equalsIgnoreCase(firstWord) && !q.thirdWord.equalsIgnoreCase(secondWord) && !q.thirdWord.equalsIgnoreCase(thirdWord)){
			this.isCorrect = false;
		}
		else{
			this.isCorrect = true;
		}
	}

	public static List<Answer> findInvolving(User user,List<Quiz> quizzes){
		List<Answer> answers = new ArrayList<Answer>();
		for(Quiz quiz:quizzes){
			answers.add(find.where().eq("user" ,user).eq("quiz",quiz).findUnique());
		}
		return answers;
	}

	public static List<Answer> findInvolving(ExperimentSchedule exp){
		List<Answer> answers = new ArrayList<Answer>();
		for(Trial trial : models.brownPeterson.Trial.findInvolving(exp)){
			for(Quiz quiz : trial.quizzes){
				answers.addAll(quiz.answers);
			}
		}
		return answers;
	}
        
        public static double calculateAverageScore(){
                List<Answer> answers = Answer.find.all();
                double sum = Answer.calculateTotalScore(answers);
                return sum/(answers.size());
        }

	public static double calculateTotalUsedTime(List<Answer> answers) {
		double totalUsedTime = 0.0;
		for(Answer ans : answers) {
			totalUsedTime += ans.usedTime;
		}
		return totalUsedTime;
	}

	public static int calculateTotalScore(List<Answer> answers) {
		int totalScore = 0;
		for(Answer answer : answers){
			if(answer.isCorrect)
				totalScore++;
		}
		return totalScore;
	}
	@SuppressWarnings("unchecked")
	public static Finder<Long, Answer> find = new Finder(Long.class, Answer.class);

}
