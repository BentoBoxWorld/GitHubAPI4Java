package world.bentobox.githubapi4java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import world.bentobox.githubapi4java.extra.Base64url;
import world.bentobox.githubapi4java.extra.CacheMode;
import world.bentobox.githubapi4java.objects.GitHubObject;
import world.bentobox.githubapi4java.objects.GitHubOrganization;
import world.bentobox.githubapi4java.objects.repository.GitHubRepository;
import world.bentobox.githubapi4java.objects.user.GitHubUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GitHub {
	
	private String token = "";
	private String hardDriveCache = null;
	private CacheMode cacheMode;
	public Map<String, JsonElement> cache = new HashMap<>();
	
	public static int ITEMS_PER_PAGE = 100;
	
	public GitHub() {
		this.cacheMode = CacheMode.RAM_CACHE;
	}
	
	public GitHub(String accessToken) {
		this.token = accessToken;
		this.cacheMode = CacheMode.RAM_CACHE;
	}
	
	public String getAccessToken() {
		return this.token;
	}
	
	public String getURL() {
		return "https://api.github.com/";
	}
	
	public GitHubUser getUser(String username) {
		return new GitHubUser(this, username);
	}
	
	public GitHubOrganization getOrganization(String username) {
		return new GitHubOrganization(this, username);
	}
	
	public GitHubRepository getRepository(String username, String repo) {
		return new GitHubRepository(this, username + "/" + repo);
	}
	
	public JsonElement call(GitHubObject object) {
		try {
			String query = getURL() + object.getURL();
			String token = getAccessToken();
			if (token != "") {
				query += "?access_token=" + token;
				
				if (object.getParameters() != null) {
					for (Map.Entry<String, String> parameter: object.getParameters().entrySet()) {
						query += "&" + parameter.getKey() + "=" + parameter.getValue();
					}
				}
			}
			else {
				if (object.getParameters() != null) {
					boolean first = true;
					
					for (Map.Entry<String, String> parameter: object.getParameters().entrySet()) {
						query += (first ? "?": "&") + parameter.getKey() + "=" + parameter.getValue();
						
						first = false;
					}
				}
			}
			
			URL website = new URL(query);
			
			HttpURLConnection connection = (HttpURLConnection) website.openConnection();
	        connection.setConnectTimeout(5000);
	        connection.addRequestProperty("User-Agent", "GitHub Web API 4 Java (by TheBusyBiscuit)");
	        connection.setDoOutput(true);
			
	        if (connection.getResponseCode() == 200) {
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        	
	        	StringBuilder buffer = new StringBuilder();
	        	
	        	String line;
	        	
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				
				reader.close();
	     	    connection.disconnect();
	     	    
	     	    JsonElement json = new JsonParser().parse(buffer.toString());
	     	    
	     	    return json;
	        }
	        else {
	     	    connection.disconnect();
	     	    
	        	JsonObject json = new JsonObject();
	        	json.addProperty("message", connection.getResponseCode() + " - " + connection.getResponseMessage());
	        	json.addProperty("documentation_url", "https://developer.github.com/v3");
	        	json.addProperty("code", connection.getResponseCode());
	        	
	        	return json;
	        }
		} catch (SocketTimeoutException e) {
        	JsonObject json = new JsonObject();
        	json.addProperty("message", e.getClass().getName() + " - " + e.getLocalizedMessage());
        	json.addProperty("documentation_url", "404");
        	json.addProperty("exception", e.getClass().getSimpleName());
        	
        	return json;
		} catch (MalformedURLException e) {
        	JsonObject json = new JsonObject();
        	json.addProperty("message", e.getClass().getName() + " - " + e.getLocalizedMessage());
        	json.addProperty("documentation_url", "404");
        	json.addProperty("exception", e.getClass().getSimpleName());
        	
        	return json;
		} catch (IOException e) {
        	JsonObject json = new JsonObject();
        	json.addProperty("message", e.getClass().getName() + " - " + e.getLocalizedMessage());
        	json.addProperty("documentation_url", "404");
        	json.addProperty("exception", e.getClass().getSimpleName());
        	
        	return json;
		}
	}
	
	public void clearCache() {
		this.cache.clear();
	}

	public void cache(String url, JsonElement response) {
		switch(this.cacheMode) {
			case HARD_DRIVE_CACHE: {
				if (hardDriveCache != null) {
					try {
						saveHardDriveCache(Base64url.encode(url) + ".json", response);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case RAM_AND_HARD_DRIVE_CACHE: {
				cache.put(url, response);
				
				if (hardDriveCache != null) {
					try {
						saveHardDriveCache(Base64url.encode(url) + ".json", response);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case RAM_CACHE: {
				cache.put(url, response);
				break;
			}
			default: {
				break;
			}
		}
	}
	
	public JsonElement readHardDriveCache(String file) throws IOException {
		if (new File(hardDriveCache + file).exists()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(hardDriveCache + file)), StandardCharsets.UTF_8));
			
			String data = reader.readLine();
			
		    reader.close();
			return new JsonParser().parse(data);
		}
		
		return null;
	}
	
	protected void saveHardDriveCache(String file, JsonElement json) throws FileNotFoundException {
		Gson gson = new GsonBuilder().serializeNulls().create();
	    PrintWriter writer = new PrintWriter(hardDriveCache + file);
	    writer.println(gson.toJson(json));
	    writer.close();
	}

	public void setupHardDriveCache(String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) dir.mkdirs();
		
		this.hardDriveCache = path + "/";
	}

	public String getHardDriveCache() {
		return hardDriveCache;
	}

	public void setCacheMode(CacheMode mode) {
		this.cacheMode = mode;
	}
	
	public CacheMode getCacheMode() {
		return this.cacheMode;
	}
}
