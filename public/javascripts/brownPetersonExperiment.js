var questionTime = 5000;
var countDownTime = 5000;
var flashTime = 5000;
var answerTime = 30000;
var startTimer = setInterval(function() {startTimerFunction()},3000);
var countdownTimer = 0;
var flashTimer = 0;
var answerTimer = 0;
var finTimer = 0;
var expDuration = 0;
var d;
var isStore = 0;
var count=30;
var counter;

function calculateTimeLeft()
{
  count=count-1;
        document.getElementById("timeLeft").innerHTML= " เวลาที่เหลือ " + (count-1) +" วินาท่ี " ;
}
function validate(evt) {
  var theEvent = evt || window.event;
  var key = theEvent.keyCode || theEvent.which;
  key = String.fromCharCode( key );
  var regex = /[0-9]/;
  if( !regex.test(key) ) {
    theEvent.returnValue = false;
    if(theEvent.preventDefault) theEvent.preventDefault();
  }
}
function startTimerFunction(){
        clearInterval(startTimer);
        document.getElementById("word1").style.visibility="visible";
        document.getElementById("word2").style.visibility="visible";
        document.getElementById("word3").style.visibility="visible";
        countdownTimer = setInterval(function() {countDownTimerFunction()},questionTime);
}

function countDownTimerFunction(){
    clearInterval(countdownTimer);
	document.getElementById("word1").style.visibility = "hidden";
	document.getElementById("word2").style.visibility = "hidden";
	document.getElementById("word3").style.visibility = "hidden";
    document.getElementById("counter").style.visibility = "visible";
    flashTimer = setInterval(function() {flashTimerFunction()},countDownTime);
}

function flashTimerFunction(){
        clearInterval(flashTimer);
        document.getElementById("counter").style.visibility = "hidden";
        document.getElementById("hopRabbit").style.visibility = "visible";
        flashTime = document.getElementById("flashTime").value;
    	answerTimer = setInterval(function() {answerTimerFunction()},flashTime);
}

function answerTimerFunction(){
        clearInterval(answerTimer);
        document.getElementById("hopRabbit").style.visibility = "hidden";
        document.getElementById("firstWord").style.visibility = "visible";
    	document.getElementById("secondWord").style.visibility = "visible";
    	document.getElementById("thirdWord").style.visibility = "visible";
    	document.getElementById("countdownResult").style.visibility = "visible";
    	document.getElementById("SubmitButton").style.visibility = "visible";
    	document.getElementById("timeLeft").style.visibility = "visible";
    	document.getElementById("ansTag").style.visibility = "visible";
    	document.getElementById("numberTag").style.visibility = "visible";
        document.answerForm.firstWord.focus();
        

        counter = setInterval(calculateTimeLeft, 1000); //1000 will  run it every 1 second
    	finTimer = setInterval(function() {finishTimerFunction()},answerTime);
    	d = new Date();
    	expDuration = d.getTime();

}

function finishTimerFunction(){
        
        clearInterval(finTimer);
        clearInterval(counter);
        submitButtonClick();
        document.forms["answerForm"].submit();
}

function submitButtonClick(){
     document.getElementById("usedTime").disabled = false;

//     if (document.getElementById("countdownResult").value == ""){
//        document.getElementById("countdownResult").value = "-99";
//     }
        d = new Date();
        expDuration = d.getTime()-expDuration;
        isStore = 1;

     document.getElementById("usedTime").value = expDuration/1000;

}

