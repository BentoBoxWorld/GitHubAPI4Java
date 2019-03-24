package world.bentobox.githubapi4java.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.objects.repository.GitHubRepository;

public class GitHubBlob extends GitHubFile {
	
	public GitHubBlob(GitHub api, GitHubRepository repo, String id) {
		super(api, repo, "/git/blobs/" + id);
	}
	
	public GitHubBlob(GitHub api, GitHubRepository repo, String id, JsonElement response) {
		super(api, repo, "/git/blobs/" + id, response);
	}

	public GitHubBlob(GitHubObject obj) {
		super(obj);
	}
	
	@Override
	public String getRawURL() {
		return ".*repos/.*/.*/git/blobs/.*";
	}

	@Override
	@GitHubAccessPoint(path = "@size", type = Integer.class, requiresAccessToken = false)
	public int getSize() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "size") ? null: response.get("size").getAsInt();
	}

	@GitHubAccessPoint(path = "@content", type = String.class, requiresAccessToken = false)
	public String getFileContent() throws IllegalAccessException {
		JsonElement element = getResponse(true);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "content") ? null: response.get("content").getAsString();
	}

	@GitHubAccessPoint(path = "@encoding", type = String.class, requiresAccessToken = false)
	public String getEncoding() throws IllegalAccessException {
		JsonElement element = getResponse(true);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "encoding") ? null: response.get("encoding").getAsString();
	}
}
