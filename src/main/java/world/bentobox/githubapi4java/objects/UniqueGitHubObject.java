package world.bentobox.githubapi4java.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;

import java.util.Date;

public class UniqueGitHubObject extends GitHubObject {

	public UniqueGitHubObject(GitHub api, GitHubObject parent, String suffix) {
		super(api, parent, suffix);
	}

	public UniqueGitHubObject(GitHubObject obj) {
		super(obj);
	}

	@GitHubAccessPoint(path = "@id", type = Integer.class, requiresAccessToken = false)
	public int getID() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "id") ? null: response.get("id").getAsInt();
	}

	@GitHubAccessPoint(path = "@created_at", type = Date.class, requiresAccessToken = false)
	public Date getCreationDate() throws IllegalAccessException {
		JsonElement element = getResponse(true);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "created_at") ? null: GitHubDate.parse(response.get("created_at").getAsString());
	}

	@GitHubAccessPoint(path = "@updated_at", type = Date.class, requiresAccessToken = false)
	public Date getLastUpdatedDate() throws IllegalAccessException {
		JsonElement element = getResponse(true);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "updated_at") ? null: GitHubDate.parse(response.get("updated_at").getAsString());
	}

}
