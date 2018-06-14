package com.amazonaws.lambda.studytime;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.QuestionList;
 
public class NoIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.NoIntent").and(Predicates.sessionAttribute(Attributes.STATE_KEY, Attributes.QUESTION_LIST_STATE)));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
     	QuestionList questions = new QuestionList(sessionAttributes);
  		String next =  questions.getNextOption();
  		if(next!=null) {
  			questions.save(sessionAttributes);
  	 		return input.getResponseBuilder()
  	 				.withSpeech("Did you mean "+next+"?")
  	 				.withReprompt("Did you mean "+next+"?")
  	 				.withShouldEndSession(false)
  	 				.build();
  		} else {
  			sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
  			return input.getResponseBuilder()
  	 				.withSpeech("I couldn't find what you were looking for. You can try again, or ask for help for more options.")
  	 				.withShouldEndSession(false)
  	 				.build();
  		}
  		
     }
 
}