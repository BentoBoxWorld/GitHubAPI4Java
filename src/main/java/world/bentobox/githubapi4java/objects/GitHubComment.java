package world.bentobox.githubapi4java.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.objects.repository.GitHubRepository;
import world.bentobox.githubapi4java.objects.user.GitHubUser;

public class GitHubComment extends UniqueGitHubObject {
	
	public GitHubComment(GitHub api, GitHubRepository repo, int id) {
		super(api, repo, "/comments/" + id);
	}
	
	public GitHubComment(GitHub api, GitHubRepository repo, int id, JsonElement response) {
		super(api, repo, "/comments/" + id);
		
		this.minimal = response;
	}

	public GitHubComment(GitHubObject obj) {
		super(obj);
	}
	
	@Override
	public String getRawURL() {
		return ".*repos/.*/.*/comments/.*";
	}
	
	@GitHubAccessPoint(path = "@user", type = GitHubUser.class, requiresAccessToken = false)
	public GitHubUser getUser() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "user") ? null: new GitHubUser(api, response.get("user").getAsJsonObject().get("login").getAsString(), response.get("user").getAsJsonObject());
	}

	@GitHubAccessPoint(path = "@body", type = String.class, requiresAccessToken = false)
	public String getMessageBody() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "body") ? null: response.get("body").getAsString();
	}
}
