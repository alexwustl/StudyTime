package com.amazonaws.lambda.studytime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.State;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class NextIntentHandler implements RequestHandler {
 
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("AMAZON.NextIntent"));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	 Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
    	 int currentQuestion = Integer.parseInt(sessionAttributes.get(Attributes.QUESTION_NUMBER).toString());
    	 State state = new State(sessionAttributes.get(Attributes.JSON_DATA).toString(),input,currentQuestion);
    	 String result = state.getNextQuestion();
    	 sessionAttributes.put(Attributes.QUESTION_NUMBER,currentQuestion+1);
         return input.getResponseBuilder()
                 .withSpeech(result)
                 .withReprompt(result)
                 .withShouldEndSession(false)
                 .build();
     }
 
}