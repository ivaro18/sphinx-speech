package domotica;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class Main {
	
	public static int LIGHT_SWITCH_INDEX = 18;
	
	public static void main(String[]  args) {
		try{
			Configuration conf = new Configuration();
		
		
			conf.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
			conf.setDictionaryPath("custom.dic");
			conf.setLanguageModelPath("custom.lm");
			
			LiveSpeechRecognizer recognize = new LiveSpeechRecognizer(conf);
			
			recognize.startRecognition(true);
			
			SpeechResult result;
			
			String apiUrl = "http://localhost:8080/json.htm?type=command&param=switchlight&idx=" + Main.LIGHT_SWITCH_INDEX;
			
			while((result = recognize.getResult()) != null ) {
				String command = result.getHypothesis();
				String urlParams = null;
				System.out.println("Hyp: " + command);
				String commandNoCase = command.toLowerCase();
				if(commandNoCase.contains("on") || commandNoCase.contains("aan")) {
					System.out.println("Turning lights on");
					urlParams = "&switchcmd=On";
				}
				else if(commandNoCase.contains("off") || commandNoCase.contains("uit")) {
					System.out.println("Turning lights off");
					urlParams = "&switchcmd=Off";
				}
				
				if(urlParams != null) {
					try{
					URL url = new URL(apiUrl + urlParams);
					URLConnection conn = url.openConnection();
					InputStream is = conn.getInputStream();
					System.out.println(is.toString());
					} catch(Exception ex) {
						System.out.println(ex.getMessage());
						continue;
					}
				}
			}
		}catch(IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
