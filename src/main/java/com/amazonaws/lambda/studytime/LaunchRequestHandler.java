package com.amazonaws.lambda.studytime;


import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.amazonaws.lambda.studytime.responses.ResponseGeneral;
import com.amazonaws.lambda.studytime.util.Attributes;
import com.amazonaws.lambda.studytime.util.State;
import com.amazonaws.lambda.studytime.util.Utility;
import com.fasterxml.jackson.databind.JsonNode;


public class LaunchRequestHandler implements RequestHandler {
	static final Logger logger = LogManager.getLogger(LaunchRequestHandler.class);
	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(Predicates.requestType(LaunchRequest.class));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
		String accessTokenTest = input
				.getRequestEnvelope()
				.getContext()
				.getSystem()
				.getUser()
				.getAccessToken();
		//TODO directives
		if(accessTokenTest != null)
		{
			String[] tokens = input
					.getRequestEnvelope()
					.getContext()
					.getSystem()
					.getUser()
					.getAccessToken()
					.split("&");
			String userId = tokens[1];
			JsonNode master = Utility.makeWebRequest("https://api.quizlet.com/2.0/users/"+userId,input);
			if(master!=null) {
				State state = new State(master);
				state.save(sessionAttributes);
				return input.getResponseBuilder()
						.withSpeech(ResponseGeneral.WELCOME)
						.withReprompt(ResponseGeneral.HELP)
						.withShouldEndSession(false)
						.build();
			} else {
				return input.getResponseBuilder()
						.withSpeech(ResponseGeneral.ERROR)
						.withShouldEndSession(true)
						.build();
			}
		} else {
			return input.getResponseBuilder()
					.withSpeech(ResponseGeneral.NO_ACCESS_TOKEN)
					.withShouldEndSession(true)
					.withLinkAccountCard()
					.build();
		}
	}

}