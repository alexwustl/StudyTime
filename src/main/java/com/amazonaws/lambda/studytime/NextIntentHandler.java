package com.amazonaws.lambda.studytime;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.State;
 
public class NextIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.NextIntent").and(Predicates.sessionAttribute(Attributes.STATE_KEY, Attributes.FLASH_STATE)));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	 Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
    	 State state = new State(sessionAttributes);
    	 String nextQuestion = state.getNextQuestion();
  		 state.save(sessionAttributes);
         return input.getResponseBuilder()
                 .withSpeech(nextQuestion)
                 .withReprompt(nextQuestion)
                 .withShouldEndSession(false)
                 .build();
     }
 
}