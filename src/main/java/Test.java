import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.lambda.studytime.util.State;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static void main(String[] args) {
		URL url;
		URL url2;
 	    HttpURLConnection connection = null;  
 	   HttpURLConnection connection2 = null;  
 	    String result = "";
 	    try {
 	      //Create connection
 	      url = new URL("https://api.quizlet.com/2.0/search/sets?q="+"chemistry");
 	      connection = (HttpURLConnection)url.openConnection();
 	      connection.setRequestProperty("Authorization", "Bearer " + "mPmCJapmj9GK4aMbW4cW8PXS2qzk49ucd9463PNQ");
 	      connection.setUseCaches(false);
 	      connection.setDoInput(true);
 	      connection.setDoOutput(true);
 	      int responseCode = connection.getResponseCode();
 	      result = responseCode+"";
 	      if(true) {
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
	 	 	  System.out.println(resultId);
	 	 	  url2 = new URL("https://api.quizlet.com/2.0/sets/"+resultId);
	 	 	  connection2 = (HttpURLConnection)url2.openConnection();
	 	      connection2.setRequestProperty("Authorization", "Bearer " + "mPmCJapmj9GK4aMbW4cW8PXS2qzk49ucd9463PNQ");
	 	      connection2.setUseCaches(false);
	 	      connection2.setDoInput(true);
	 	      connection2.setDoOutput(true);
	 	      InputStream is2 = connection2.getInputStream();
	 	      BufferedReader rd2 = new BufferedReader(new InputStreamReader(is2));
	 	      String line2;
	 	      StringBuffer response2 = new StringBuffer(); 
	 	      while((line2 = rd2.readLine()) != null) {
	 	        response2.append(line2);
	 	        response2.append('\r');
	 	      }
	 	      rd2.close();
	 	      result = response2.toString();
	 	      System.out.println(result);
	 	      mapper =  new ObjectMapper();
	 	      data = mapper.readTree(response.toString());
	 	      State state = new State(data);
 	 	      //sessionAttributes.put(Attributes.JSON_DATA,state);
 	      }
 	     
 	    } catch (Exception e) {
 	     // error = e.getLocalizedMessage();
 	    	e.printStackTrace();
 	    } finally {
 	      if(connection != null) {
 	        connection.disconnect(); 
 	      }
 	     if(connection2 != null) {
  	        connection2.disconnect(); 
  	      }
 	    }
 	    System.out.println(result);
	}
}
