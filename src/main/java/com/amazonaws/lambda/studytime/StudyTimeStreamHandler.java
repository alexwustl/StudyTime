package com.amazonaws.lambda.studytime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

public class StudyTimeStreamHandler extends SkillStreamHandler{
	static final Logger logger = LogManager.getLogger(StudyTimeStreamHandler.class);
	 private static Skill getSkill() {
         return Skills.standard()
                 .addRequestHandlers(new CancelIntentHandler(), new FlipoverHandler(), new FallbackIntentHandler(), new HelpIntentHandler(), new LaunchRequestHandler(), new NextIntentHandler(), new NoIntentHandler(),new PreviousIntentHandler(),new RepeatIntentHandler(),new SearchActionHandler(), new ShuffleOnIntentHandler(),new StopIntentHandler(),new YesIntentHandler(),new EndSessionRequest(),new StartOverIntentHandler())
                 .build();
     }
 
     public StudyTimeStreamHandler() {
         super(getSkill());
     }
}
