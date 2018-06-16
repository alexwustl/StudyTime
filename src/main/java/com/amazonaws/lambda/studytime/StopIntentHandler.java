package com.amazonaws.lambda.studytime;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
 
public class StopIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.StopIntent"));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
         return input.getResponseBuilder()
             .withSpeech("Thank you for using Study Time.")
             .withShouldEndSession(true)
             .build();
     }
 
}