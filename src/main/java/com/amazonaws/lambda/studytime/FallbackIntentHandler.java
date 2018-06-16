package com.amazonaws.lambda.studytime;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.responses.ResponseGeneral;
 
public class FallbackIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.FallbackIntent"));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
         return input.getResponseBuilder()
                 .withSpeech("I'm sorry I didn't understand that, if you are studying and want the answer and the next question, say next.")
                 .withReprompt(ResponseGeneral.HELP)
                 .withShouldEndSession(false)
                 .build();
     }
 
}