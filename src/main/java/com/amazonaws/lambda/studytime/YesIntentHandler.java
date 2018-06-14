package com.amazonaws.lambda.studytime;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.QuestionList;
import com.amazonaws.lambda.studytime.util.State;
 
public class YesIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.YesIntent").and(Predicates.sessionAttribute(Attributes.STATE_KEY, Attributes.QUESTION_LIST_STATE)));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
    	QuestionList questions = new QuestionList(sessionAttributes);
 		String chosen = questions.getYes();
 		State state = new State(sessionAttributes);
 		state.changeSet(chosen, input);
 		sessionAttributes.put(Attributes.STATE_KEY, Attributes.FLASH_STATE);
 		String firstQuestion = state.getNextQuestion();
 		state.save(sessionAttributes);
		return input.getResponseBuilder()
				.withSpeech("Here is your first flashcard. "+firstQuestion)
				.withReprompt(firstQuestion)
				.withShouldEndSession(false)
				.build();
     }
 
}