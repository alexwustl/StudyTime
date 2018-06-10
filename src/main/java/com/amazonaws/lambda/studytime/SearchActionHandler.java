package com.amazonaws.lambda.studytime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.responses.ResponseGeneral;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.State;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class SearchActionHandler implements RequestHandler {
	static final Logger logger = LogManager.getLogger(SearchActionHandler.class);
     @Override
     public boolean canHandle(HandlerInput input) {
    	 return input.matches(Predicates.intentName("Search"));
     }
 
     @Override
     public Optional<Response> handle(HandlerInput input) {
    	 Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
    	 IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
  	  	 Slot slot = (Slot) intentRequest.getIntent().getSlots().get("flashCardSet");
  	  	 String request = slot.getValue();
    	 String accessToken = input
                 .getRequestEnvelope()
                 .getContext()
                 .getSystem()
                 .getUser()
                 .getAccessToken();
     	URL url;
 	    HttpURLConnection connection = null;  
 	    String result = "";
 	    try {
 	      //Create connection
 	      url = new URL("https://api.quizlet.com/2.0/search/sets?q="+request);
 	      connection = (HttpURLConnection)url.openConnection();
 	      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
 	      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
 	      connection.setRequestProperty("Content-Language", "en-US");  
 	      connection.setUseCaches(false);
 	      connection.setDoInput(true);
 	      connection.setDoOutput(true);
 	      int responseCode = connection.getResponseCode();
 	      result = responseCode+"";
 	      if(responseCode == HttpURLConnection.HTTP_OK) {
 	    	 //Get Response	
 	    	 InputStream is = connection.getInputStream();
	 	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	 	      String line;
	 	      StringBuffer response = new StringBuffer(); 
	 	      while((line = rd.readLine()) != null) {
	 	        response.append(line);
	 	        response.append('\r');
	 	      }
	 	      rd.close();
	 	      result = response.toString();
	 	      System.out.println(result);
	 	      ObjectMapper mapper =  new ObjectMapper();
	 	      JsonNode data = mapper.readTree(response.toString());
	 	      String resultId = "";
	 		  resultId=data.get("sets").iterator().next().get("id").toString();
	 	 	  State state = new State(resultId,input,0);
	 	      sessionAttributes.put(Attributes.JSON_DATA,resultId);
	 	      sessionAttributes.put(Attributes.STATE_KEY, Attributes.FLASH_STATE);
	 	      sessionAttributes.put(Attributes.QUESTION_NUMBER,1);
	 	      result = state.getNextQuestion();
 	      }
 	     
 	    } catch (Exception e) {
 	    	e.printStackTrace();
 	    	result = ResponseGeneral.ERROR;
 	    } finally {
 	      if(connection != null) {
 	        connection.disconnect(); 
 	      }
 	    }
 	    return input.getResponseBuilder()
       		 .withSpeech(result)
             .withReprompt(ResponseGeneral.HELP)
             .withShouldEndSession(false)
             .build();
     }
 
}