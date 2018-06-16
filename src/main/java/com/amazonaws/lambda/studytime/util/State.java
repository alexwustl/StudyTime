package com.amazonaws.lambda.studytime.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
public class State {
	static final Logger logger = LogManager.getLogger(State.class);
	private ArrayNode data;
	private ArrayNode currentSet;
	private boolean readFront;
	private int currentQuestion;
	/*New*/
	public State(JsonNode input) {
		readFront = true;
		currentQuestion = 0;
		currentSet = null;
		data = JsonNodeFactory.instance.arrayNode();
		JsonNode array = input.get("sets");
		if(array.isArray()) {
			for(final JsonNode obj: array){
				ObjectNode value = JsonNodeFactory.instance.objectNode();
				value.set("id", obj.get("id"));
				value.set("title", obj.get("title"));
				data.add(value);
			}
		}
	}
	/*Restore*/
	public State(Map<String, Object> sessionAttributes) {
		//Because the arrays were being reserialized as ArrayLists, this guarantees correct class
		ObjectMapper mapper = new ObjectMapper();
		this.data = mapper.valueToTree(sessionAttributes.get("data"));
		this.currentSet=mapper.valueToTree(sessionAttributes.get("currentSet"));
		this.readFront= (boolean) sessionAttributes.get("readFront");
		this.currentQuestion=(int) sessionAttributes.get("currentQuestion");
	}
	public String[] changeSetOptions(String setName) {
		ArrayList<String> titles = new ArrayList<String>();
		for(JsonNode node:data) {
			String result = node.get("title").toString();
			titles.add(result.substring(1, result.length()-1));
		}
		return Utility.getClosestSets(titles.toArray(new String[0]), setName);
	}
	public void changeSet(String setName,HandlerInput input) {
		for(JsonNode node:data) {
			if(node.get("title").toString().equals(setName)) {
				JsonNode webNode = Utility.makeWebRequest("https://api.quizlet.com/2.0/sets/"+node.get("id"), input);
				if(webNode!=null) {
					currentSet = (ArrayNode) webNode.get("terms");
				} else {
					currentSet = JsonNodeFactory.instance.arrayNode();
				}	
				currentQuestion = 0;
				return;
			}
		}
	}
	public boolean isReadFront() {
		return readFront;
	}
	public boolean flipOver() {
		readFront=!readFront;
		return readFront;
	}
	public String getQuestion() {
		return readFront ? getFront(currentQuestion):getBack(currentQuestion);
	}
	public String getAnswer(int i) {
		return readFront ? getBack(i):getFront(i);
	}
	public String getNextQuestion() {
		String result = "";
		if(currentQuestion>0&&currentQuestion<=currentSet.size()) {
			result= "The answer to the previous question was "+ (readFront?getBack(currentQuestion-1):getFront(currentQuestion-1))+".";
		}
		 if(currentQuestion == currentSet.size()) {
			result+=" That was the last card. You can start over, or pick a new set to study from.";
			currentQuestion++;
		} else if(currentQuestion==currentSet.size()+1){
			result="You have reached the end of this set. You can start over, or pick a new set to study from.";
		} else {
			result+=" The next card reads "+(readFront?getFront(currentQuestion):getBack(currentQuestion))+".";
			currentQuestion++;
		}
		 return result;
	}
	
	public String getPreviousQuestion() {
		String result = "";
		if(currentQuestion>1) {
			currentQuestion--;
			result= "The question is "+ (readFront?getFront(currentQuestion-1):getBack(currentQuestion-1))+".";
		} else {
			result="You are already at the beginning, you can say repeat to repeat the question.";
		}	 
		 return result;
	}
	
	public String repeatQuestion() {
		if(currentQuestion!=currentSet.size()+1) {
			currentQuestion--;
		} 
		return getNextQuestion();
	}
	
	public String getFront(int i) {
		return currentSet.get(i).get("term").toString();
	}
	public String getBack(int i) {
		return currentSet.get(i).get("definition").toString();
	}
	public void shuffle() {
		currentQuestion=0;
		ArrayList<JsonNode> nodes = new ArrayList<JsonNode>(currentSet.size());
		Iterator<JsonNode> nodeIterator = currentSet.elements();
		while(nodeIterator.hasNext()) {
			nodes.add(nodeIterator.next());
		}
		Collections.shuffle(nodes);
		ObjectMapper mapper = new ObjectMapper();
		currentSet = mapper.valueToTree(nodes);
	}
	public void startOver() {
		currentQuestion=0;
	}
	public void save(Map<String, Object> sessionAttributes) {
		sessionAttributes.put("data", data);
		sessionAttributes.put("currentSet", currentSet);
		sessionAttributes.put("readFront", readFront);
		sessionAttributes.put("currentQuestion",currentQuestion);
	}
}
