package com.amazonaws.lambda.studytime;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.responses.ResponseGeneral;
import com.amazonaws.lambda.studytime.util.Attributes;
 
public class CancelIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.CancelIntent"));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	 Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
    	 sessionAttributes.put(Attributes.STATE_KEY,Attributes.START_STATE);
         return input.getResponseBuilder()
                 .withSpeech("Cancelling")
                 .withReprompt(ResponseGeneral.HELP)
                 .withShouldEndSession(false)
                 .build();
     }
 
}