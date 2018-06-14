package com.amazonaws.lambda.studytime.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class QuestionList {

	private final ArrayNode options;
	private int currentOption;

	public QuestionList(String[] options) {
		ObjectMapper mapper = new ObjectMapper();
		this.options = mapper.valueToTree(options);
		this.currentOption=0;
	}

	public QuestionList(Map<String,Object> sessionAttributes) {
		ObjectMapper mapper = new ObjectMapper();
		this.options = mapper.valueToTree(sessionAttributes.get("options"));
		this.currentOption = (int) sessionAttributes.get("currentOption");
	}

	public String getNextOption() {
		if(currentOption>=options.size()) {
			return null;
		}
		return options.get(currentOption++).toString();
	}
	public void save(Map<String,Object> sessionAttributes) {
		sessionAttributes.put("options",this.options);
		sessionAttributes.put("currentOption", currentOption);
	}
	public String getYes() {
		return options.get(currentOption-1).toString();
	}
}
