package models.brownPeterson;
import models.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
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

	@ManyToOne
	public User user;
	@ManyToOne
	public Quiz quiz;

	public Answer(String firstWord,String secondWord,String thirdWord,double usedTime,String countdownResult, User user, Quiz quiz){
		this.firstWord = firstWord;
		this.secondWord = secondWord;
		this.thirdWord = thirdWord;
		this.usedTime = usedTime;
		this.countdownResult = countdownResult;
		this.user = user;
		this.quiz = quiz;
	}

	public static List<Answer> findInvolving(User user,List<Quiz> quizzes){
		List<Answer> answers = new ArrayList<Answer>();
		for(Quiz quiz:quizzes){
			answers.add(find.where().eq("user" ,user).eq("quiz",quiz).findUnique());
		}
		return answers;
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
			Question q = answer.quiz.question;
			List<String> q_words = new ArrayList<String>();
			q_words.add(q.firstWord);
			q_words.add(q.secondWord);
			q_words.add(q.thirdWord);

			List<String> a_words = new ArrayList<String>();
			a_words.add(answer.firstWord);
			a_words.add(answer.secondWord);
			a_words.add(answer.thirdWord);

			for(String go : a_words){
				String temp = null;
				for(String word : q_words){
					if(go.equals(word)){
						temp = word;
					}		
				}
				if(temp != null){
					q_words.remove(temp);
				}else{
					break;
				}
			}

			if(q_words.size() == 0){
				totalScore++;
			}
		}
		return totalScore;
	}

	@SuppressWarnings("unchecked")
	public static Finder<Long, Answer> find = new Finder(Long.class, Answer.class);

}
