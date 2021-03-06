package world.bentobox.githubapi4java.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.objects.repository.GitHubRepository;

public class GitHubLanguage extends GitHubObject {

	private String name;
	private GitHubRepository repo;
	
	public GitHubLanguage(GitHub api, GitHubRepository repo, String name) {
		super(api, repo, "/languages");
		
		this.name = name;
		this.repo = repo;
	}
	
	public GitHubLanguage(GitHub api, GitHubRepository repo, String name, JsonElement response) {
		super(api, repo, "/languages");
		
		this.name = name;
		this.repo = repo;
		
		this.minimal = response;
	}
	
	public GitHubRepository getRepository() {
		return this.repo;
	}
	
	@Override
	public String getRawURL() {
		return ".*repos/.*/.*/languages";
	}
	
	public String getName() {
		return this.name;
	}

	public int getSize() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, getName()) ? null: response.get(getName()).getAsInt();
	}

}
