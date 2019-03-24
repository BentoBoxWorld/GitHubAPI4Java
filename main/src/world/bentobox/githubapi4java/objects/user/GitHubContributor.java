package world.bentobox.githubapi4java.objects.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.objects.GitHubObject;

public class GitHubContributor extends GitHubUser {

	public GitHubContributor(GitHub api, String username, JsonElement response) {
		super(api, username, response);
	}

	public GitHubContributor(GitHubObject obj) {
		super(obj);
	}

	@GitHubAccessPoint(path = "@contributions", type = Integer.class, requiresAccessToken = false)
	public int getContributionsAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "contributions") ? null: response.get("contributions").getAsInt();
	}

}
