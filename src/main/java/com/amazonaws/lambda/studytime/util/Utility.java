package com.amazonaws.lambda.studytime.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

	public static JsonNode makeWebRequest(String urlText, HandlerInput input) {
		JsonNode result = null;
		String[] tokens = input
				.getRequestEnvelope()
				.getContext()
				.getSystem()
				.getUser()
				.getAccessToken()
				.split("&");
		String accessToken = tokens[0];
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(urlText);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Content-Language", "en-US");  
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer(); 
				while((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				ObjectMapper mapper=new ObjectMapper();
				JsonNode master = mapper.readTree(response.toString());
				result= master;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		return result;
	}

	public static String[] getClosestSets(String[] possible,String query) {
		final int TOLERANCE = 10;
		//		String[] words = query.split(" ");
		//		Map<String,Integer> matches = new HashMap<String, Integer>();
		//		for(int i=0;i<possible.length;i++) {
		//			matches.put(possible[i], 0);
		//			String[] possibleWords = possible[i].split(" ");
		//			for(int j=0;j<possibleWords.length;j++) {
		//				for(int k=0;k<words.length;k++) {
		//					if(possibleWords[j].toLowerCase().equals(words[k].toLowerCase())) {
		//						matches.put(possible[i],matches.get(possible[i])+1);
		//					}
		//				}
		//			}
		//		}
		//		ArrayList<String> resultList = new ArrayList<String>();
		//		for (Map.Entry<String, Integer> entry : matches.entrySet()) {
		//			if(entry.getValue()>0) {
		//				resultList.add(entry.getKey());
		//			}
		//		}
		//		//At least one match was found return (or no matches possible)
		//		if(resultList.size()>0 || possible.length==0) {
		//			return resultList.toArray(new String[0]);
		//		}
		Map<String,Integer> values = new HashMap<String,Integer>();
		query=query.toLowerCase();
		int least = Integer.MAX_VALUE;
		for(int i=0;i<possible.length;i++) {
			int calc = distance(possible[i].toLowerCase(),query);
			values.put(possible[i], calc);
			least = Math.min(least, calc);
		}
		//Sort values to guarantee the better answers are first
		LinkedHashMap<String,Integer> valuesSorted = sortByComparator(values);
		ArrayList<String> result = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:valuesSorted.entrySet()) {
			if(entry.getValue()<=least+TOLERANCE) {
				result.add(entry.getKey());
			}
		}
		return result.toArray(new String[0]);

	}
	/*Base Code from rosetta code http://rosettacode.org/wiki/Levenshtein_distance#Java
	 * Tweak SUBSTITUTION_COST as needed for desired accuracy*/
	private static int distance(String a, String b) {
		final int SUBSTITUTION_COST = 2;
		a = a.toLowerCase();
		b = b.toLowerCase();
		// i == 0
		int [] costs = new int [b.length() + 1];
		for (int j = 0; j < costs.length; j++)
			costs[j] = j;
		for (int i = 1; i <= a.length(); i++) {
			// j == 0; nw = lev(i - 1, j)
			costs[0] = i;
			int nw = i - 1;
			for (int j = 1; j <= b.length(); j++) {
				int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + SUBSTITUTION_COST);
				nw = costs[j];
				costs[j] = cj;
			}
		}
		return costs[b.length()];
	}

	/*https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	 * All credit to Rais Alam*/
	private static LinkedHashMap<String, Integer> sortByComparator(Map<String, Integer> unsortMap)
	{

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
		{
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2)
			{
				return o1.getValue().compareTo(o2.getValue());

			}
		});

		// Maintaining insertion order with the help of LinkedList
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
