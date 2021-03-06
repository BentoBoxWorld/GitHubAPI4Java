package world.bentobox.githubapi4java.objects.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.objects.GitHubDate;
import world.bentobox.githubapi4java.objects.GitHubLabel;
import world.bentobox.githubapi4java.objects.GitHubObject;
import world.bentobox.githubapi4java.objects.user.GitHubUser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitHubMilestone extends RepositoryFeature {
	
	GitHubRepository repo;
	
	public GitHubMilestone(GitHub api, GitHubRepository repo, int id) {
		super(api, repo, "/milestones/" + id);
		
		this.repo = repo;
	}
	
	public GitHubMilestone(GitHub api, GitHubRepository repo, int id, JsonElement response) {
		super(api, repo, "/milestones/" + id);
		
		this.repo = repo;
		this.minimal = response;
	}

	public GitHubMilestone(GitHubObject obj) {
		super(obj);
	}
	
	public GitHubRepository getRepository() {
		return this.repo;
	}
	
	@Override
	public String getRawURL() {
		return ".*repos/.*/.*/milestones/.*";
	}
	
	@GitHubAccessPoint(path = "@creator", type = GitHubUser.class, requiresAccessToken = false)
	public GitHubUser getCreator() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "creator") ? null: new GitHubUser(api, response.get("creator").getAsJsonObject().get("login").getAsString(), response.get("creator").getAsJsonObject());
	}

	@GitHubAccessPoint(path = "@description", type = String.class, requiresAccessToken = false)
	public String getDescription() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "description") ? null: response.get("description").getAsString();
	}

	@GitHubAccessPoint(path = "/labels", type = GitHubLabel.class, requiresAccessToken = false)
	public List<GitHubLabel> getLabels() throws IllegalAccessException, UnsupportedEncodingException {
		GitHubObject repos = new GitHubObject(api, this, "/labels");
		JsonElement response = repos.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubLabel> list = new ArrayList<GitHubLabel>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubLabel label = new GitHubLabel(api, getRepository(), object.get("name").getAsString(), object);
	    	list.add(label);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "@open_issues", type = Integer.class, requiresAccessToken = false)
	public int getOpenIssuesAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "open_issues") ? null: response.get("open_issues").getAsInt();
	}

	@GitHubAccessPoint(path = "@closed_issues", type = Integer.class, requiresAccessToken = false)
	public int getClosedIssuesAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "closed_issues") ? null: response.get("closed_issues").getAsInt();
	}
	
	@GitHubAccessPoint(path = "@due_on", type = Date.class, requiresAccessToken = false)
	public Date getDueDate() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "due_on") ? null: GitHubDate.parse(response.get("due_on").getAsString());
	}
}
