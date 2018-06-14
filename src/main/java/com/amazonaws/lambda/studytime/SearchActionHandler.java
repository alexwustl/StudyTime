package com.amazonaws.lambda.studytime;

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
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.QuestionList;
import com.amazonaws.lambda.studytime.util.State;

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
		State state = new State(sessionAttributes);
		QuestionList questions = new QuestionList(state.changeSetOptions(request));
		String next = questions.getNextOption();
		if(next==null) {
			return input.getResponseBuilder()
					.withSpeech("I was unable to find any options in your sets, please try again or rephrase your question")
					.withShouldEndSession(false)
					.build();
		} else {
			sessionAttributes.put(Attributes.STATE_KEY,Attributes.QUESTION_LIST_STATE);
			questions.save(sessionAttributes);
			return input.getResponseBuilder()
					.withSpeech("Were you looking for "+next+"?")
					.withShouldEndSession(false)
					.build();
		}

	}

}