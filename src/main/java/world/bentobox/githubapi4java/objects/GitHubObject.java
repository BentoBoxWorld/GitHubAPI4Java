package world.bentobox.githubapi4java.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.extra.Base64url;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GitHubObject {
	
	protected GitHub api;
	protected GitHubObject parent;
	protected String suffix;
	
	protected JsonElement response = null;
	protected JsonElement minimal = null;
	
	private boolean debug = false;

	public GitHubObject(GitHub api, GitHubObject parent, String suffix) {
		this.api = api;
		this.parent = parent;
		this.suffix = suffix;
	}

	public GitHubObject(GitHubObject object) {
		this.api = object.api;
		this.parent = object.parent;
		this.suffix = object.suffix;
	}
	
	public void debug(boolean debug) {
		this.debug = debug;
	}

	public String getURL() {
		return this.getURL(suffix);
	}
	
	public String getFullURL() {
		String query = getURL();
		
		if (getParameters() != null) {
			boolean first = true;
			
			for (Map.Entry<String, String> parameter: getParameters().entrySet()) {
				query += (first ? "?": "&") + parameter.getKey() + "=" + parameter.getValue();
				
				first = false;
			}
		}
		
		return query;
	}

	@GitHubAccessPoint(path = "@url", type = String.class, requiresAccessToken = false)
	protected String getURL(String suffix) {
		if (parent == null) {
			return suffix;
		}
		
		StringBuilder builder = new StringBuilder();

		builder.append(new StringBuilder(suffix).reverse().toString());
		
		GitHubObject o = getParent();
		while (o != null) {
			builder.append(new StringBuilder(o.getSuffix()).reverse().toString());
			
			o = o.getParent();
		}
		
		return builder.reverse().toString();
	}

	public String getRawURL() {
		return this.getURL();
	}
	
	public JsonElement getRawResponseAsJson() {
		return this.getResponse(true);
	}
	
	public JsonElement getResponse(boolean full) {
		log("Pinging '" + getFullURL() + "'");
		if (!full && minimal != null) {
			log(" Returned locally cached (minfiied!) version.");
			return minimal;
		}
		
		if (response != null) {
			if (response.isJsonObject() && ((JsonObject) response).has("documentation_url")) {
				log(" Connection failed!");
				return null;
			}

			log(" Returned locally cached (full!) version.");
			return response;
		}
		
		if (api.cache.containsKey(getFullURL())) {
			log(" Returned globally cached (full!) version.");
			response = api.cache.get(getFullURL());
		}
		else if (api.getHardDriveCache() != null && new File(api.getHardDriveCache() + Base64url.encode(getFullURL()) + ".json").exists()) {
			log(" Returned hard drive cached (full!) version.");
			try {
				response = api.readHardDriveCache(Base64url.encode(getFullURL()) + ".json");
			} catch (IOException e) {
				log(" Establishing Connection...");
				this.response = api.call(this);
				
				if (response.isJsonObject() && ((JsonObject) response).has("documentation_url")) {
					log(" Connection failed!");
					return null;
				}

				log(" Caching Object.");
				api.cache(getFullURL(), response);
			}
		}
		else {
			log(" Establishing Connection...");
			this.response = api.call(this);
			
			if (response.isJsonObject() && ((JsonObject) response).has("documentation_url")) {
				log(" Connection failed!");
				return null;
			}

			log(" Caching Object.");
			api.cache(getFullURL(), response);
		}
		
		return response;
	}
	
	protected void log(String message) {
		if (debug) {
			System.out.println(message);
		}
	}

	protected boolean isInvalid(JsonObject response, String key) {
		if (!response.has(key)) {
			return true;
		}
		if (response.get(key).isJsonNull()) {
			return true;
		}
		if (response.get(key) == null) {
			return true;
		}
		
		return false;
	}

	protected String getSuffix() {
		return this.suffix;
	}

	protected GitHubObject getParent() {
		return this.parent;
	}
	
	public void clearCache() {
		String url = getFullURL();
		api.cache.remove(url);
		
		if (response != null) {
			response = null;
		}
	}

	public Map<String, String> getParameters() {
		return null;
	}
}
