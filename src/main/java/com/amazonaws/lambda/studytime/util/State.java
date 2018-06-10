package com.amazonaws.lambda.studytime.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class State {

	private String[] fronts;
	private String[] backs;
	private boolean readFront;
	private int currentQuestion;
	public State(String input, HandlerInput inputHandler,int current) {
		super();
		URL url;
 	    HttpURLConnection connection = null;  
		try {
			 String accessToken = inputHandler
	                 .getRequestEnvelope()
	                 .getContext()
	                 .getSystem()
	                 .getUser()
	                 .getAccessToken();
	    	  url = new URL("https://api.quizlet.com/2.0/sets/"+input);
		 	  connection = (HttpURLConnection)url.openConnection();
		      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		      connection.setUseCaches(false);
		      connection.setDoInput(true);
		      connection.setDoOutput(true);
		      InputStream is = connection.getInputStream();
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		      String line="";
		      StringBuffer response = new StringBuffer(); 
		      while((line = rd.readLine()) != null) {
		        response.append(line);
		        response.append('\r');
		      }
		      rd.close();
		      ObjectMapper mapper=new ObjectMapper();
		      JsonNode master = mapper.readTree(response.toString());
		      ArrayList<String> fronts = new ArrayList<String>();
				ArrayList<String> backs = new ArrayList<String>();
				this.readFront = true;
				Iterator<JsonNode> terms = master.get("terms").iterator();
				while(terms.hasNext()) {
					JsonNode term = terms.next();
					fronts.add(term.get("term").toString());
					backs.add(term.get("definition").toString());
			    }
				currentQuestion=current;
				this.fronts = fronts.toArray(new String[0]);
				this.backs = backs.toArray(new String[0]);
	    	 }catch(Exception e) {
	    		 e.printStackTrace();
	    	 }
		
		
	}
	public boolean isReadFront() {
		return readFront;
	}
	public void setReadFront(boolean readFront) {
		this.readFront = readFront;
	}
	public String getQuestion(int i) {
		return readFront ? fronts[i]:backs[i];
	}
	public String getAnswer(int i) {
		return readFront ? backs[i]:fronts[i];
	}
	public String getNextQuestion() {
		String result = "";
		if(currentQuestion>0) {
			result= "The answer to the previous question was "+ (readFront?backs[currentQuestion-1]:fronts[currentQuestion-1])+".";
		}
		 if(currentQuestion == fronts.length-1) {
			result+=" That was the last card. I am going to start over at the beginning.";
			currentQuestion=0;
		} else {
			result+=" The next card reads "+(readFront?fronts[currentQuestion]:backs[currentQuestion])+".";
			currentQuestion++;
		}
		 
		 return result;
	}
	
	public String repeatQuestion() {
		if(currentQuestion==0) {
			currentQuestion=fronts.length-1;
		} else {
			currentQuestion--;
		}
		return getNextQuestion();
	}
	
}
