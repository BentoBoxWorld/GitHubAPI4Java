package world.bentobox.githubapi4java.objects.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import world.bentobox.githubapi4java.GitHub;
import world.bentobox.githubapi4java.annotations.GitHubAccessPoint;
import world.bentobox.githubapi4java.objects.GitHubBranch;
import world.bentobox.githubapi4java.objects.GitHubComment;
import world.bentobox.githubapi4java.objects.GitHubCommit;
import world.bentobox.githubapi4java.objects.GitHubDate;
import world.bentobox.githubapi4java.objects.GitHubDownload;
import world.bentobox.githubapi4java.objects.GitHubLabel;
import world.bentobox.githubapi4java.objects.GitHubLanguage;
import world.bentobox.githubapi4java.objects.GitHubObject;
import world.bentobox.githubapi4java.objects.GitHubReference;
import world.bentobox.githubapi4java.objects.GitHubTag;
import world.bentobox.githubapi4java.objects.UniqueGitHubObject;
import world.bentobox.githubapi4java.objects.user.GitHubCollaborator;
import world.bentobox.githubapi4java.objects.user.GitHubContributor;
import world.bentobox.githubapi4java.objects.user.GitHubUser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitHubRepository extends UniqueGitHubObject {
	
	String fullname = null;
	
	public GitHubRepository(GitHub api, String username, String repo) {
		super(api, null, "repos/" + username + "/" + repo);
		
		this.fullname = username + "/" + repo;
	}
	
	public GitHubRepository(GitHub api, String name) {
		super(api, null, "repos/" + name);
		
		this.fullname = name;
	}

	public GitHubRepository(GitHubObject obj) {
		super(obj);
	}
	
	public GitHubRepository(GitHub api, String name, JsonElement response) {
		super(api, null, "repos/" + name);
		
		this.fullname = name;
		this.minimal = response;
	}
	
	@Override
	public String getRawURL() {
		return ".*repos/.*/.*";
	}

	@GitHubAccessPoint(path = "/forks", type = GitHubRepository.class, requiresAccessToken = false)
	public List<GitHubRepository> getForks() throws IllegalAccessException {
		return getForks(1);
	}

	@GitHubAccessPoint(path = "/forks", type = GitHubRepository.class, requiresAccessToken = false)
	public List<GitHubRepository> getAllForks() throws IllegalAccessException {
		List<GitHubRepository> forks = new ArrayList<>();
		
		int i = 2;
		List<GitHubRepository> temp = getForks(1);
		
		while (!temp.isEmpty()) {
			forks.addAll(temp);
			
			temp = getForks(i);
			i++;
		}
		
		return forks;
	}

	@GitHubAccessPoint(path = "/forks", type = GitHubRepository.class, requiresAccessToken = false)
	public List<GitHubRepository> getForks(final int page) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<>();
		params.put("page", String.valueOf(page));
		params.put("per_page", String.valueOf(GitHub.ITEMS_PER_PAGE));
		
		GitHubObject forks = new GitHubObject(api, this, "/forks") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		
		JsonElement response = forks.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubRepository> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubRepository repo = new GitHubRepository(api, object.get("full_name").getAsString(), object);
	    	list.add(repo);
	    }
		
		return list;
	}
	
	@GitHubAccessPoint(path = "/branches", type = GitHubBranch.class, requiresAccessToken = false)
	public List<GitHubBranch> getBranches() throws IllegalAccessException, UnsupportedEncodingException {
		return getBranches(1);
	}

	@GitHubAccessPoint(path = "/branches", type = GitHubBranch.class, requiresAccessToken = false)
	public List<GitHubBranch> getAllBranches() throws IllegalAccessException, UnsupportedEncodingException {
		List<GitHubBranch> branches = new ArrayList<>();
		
		int i = 2;
		List<GitHubBranch> temp = getBranches(1);
		
		while (!temp.isEmpty()) {
			branches.addAll(temp);
			
			temp = getBranches(i);
			i++;
		}
		
		return branches;
	}

	@GitHubAccessPoint(path = "/branches", type = GitHubBranch.class, requiresAccessToken = false)
	public List<GitHubBranch> getBranches(final int page) throws IllegalAccessException, UnsupportedEncodingException {
		final Map<String, String> params = new HashMap<>();
		params.put("page", String.valueOf(page));
		params.put("per_page", String.valueOf(GitHub.ITEMS_PER_PAGE));
		GitHubObject branches = new GitHubObject(api, this, "/branches") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = branches.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + branches.getURL() + "'");
		}
		
		List<GitHubBranch> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubBranch branch = new GitHubBranch(api, this, object.get("name").getAsString(), object);
	    	list.add(branch);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/stargazers", type = GitHubUser.class, requiresAccessToken = false)
	public List<GitHubUser> getStargazers() throws IllegalAccessException {
		GitHubObject users = new GitHubObject(api, this, "/stargazers");
		JsonElement response = users.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubUser> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubUser user = new GitHubUser(api, object.get("login").getAsString(), object);
	    	list.add(user);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/subscribers", type = GitHubUser.class, requiresAccessToken = false)
	public List<GitHubUser> getSubscribers() throws IllegalAccessException {
		GitHubObject users = new GitHubObject(api, this, "/subscribers");
		JsonElement response = users.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + users.getURL() + "'");
		}
		
		List<GitHubUser> list = new ArrayList<GitHubUser>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubUser user = new GitHubUser(api, object.get("login").getAsString(), object);
	    	list.add(user);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/contributors", type = GitHubContributor.class, requiresAccessToken = false)
	public List<GitHubContributor> getContributors() throws IllegalAccessException {
		GitHubObject users = new GitHubObject(api, this, "/contributors");
		JsonElement response = users.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + users.getURL() + "'");
		}
		
		List<GitHubContributor> list = new ArrayList<GitHubContributor>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubContributor user = new GitHubContributor(api, object.get("login").getAsString(), object);
	    	list.add(user);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/collaborators", type = GitHubCollaborator.class, requiresAccessToken = true)
	public List<GitHubCollaborator> getCollaborators() throws IllegalAccessException {
		GitHubObject users = new GitHubObject(api, this, "/collaborators");
		JsonElement response = users.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + users.getURL() + "'");
		}
		
		List<GitHubCollaborator> list = new ArrayList<GitHubCollaborator>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubCollaborator user = new GitHubCollaborator(api, object.get("login").getAsString(), object);
	    	list.add(user);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/languages", type = GitHubLanguage.class, requiresAccessToken = false)
	public List<GitHubLanguage> getLanguages() throws IllegalAccessException {
		GitHubObject langs = new GitHubObject(api, this, "/languages");
		JsonElement response = langs.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + langs.getURL() + "'");
		}
		
		List<GitHubLanguage> list = new ArrayList<GitHubLanguage>();
		JsonObject object = response.getAsJsonObject();
		
		for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
			list.add(new GitHubLanguage(api, this, entry.getKey(), object));
		}
		
		return list;
	}

	@GitHubAccessPoint(path = "/assignees", type = GitHubUser.class, requiresAccessToken = false)
	public List<GitHubUser> getAvailableAssignees() throws IllegalAccessException {
		GitHubObject users = new GitHubObject(api, this, "/assignees");
		JsonElement response = users.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + users.getURL() + "'");
		}
		
		List<GitHubUser> list = new ArrayList<GitHubUser>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubUser user = new GitHubUser(api, object.get("login").getAsString(), object);
	    	list.add(user);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/commits", type = GitHubCommit.class, requiresAccessToken = false)
	public List<GitHubCommit> getCommits() throws IllegalAccessException {
		return this.getCommits(1);
	}

	@GitHubAccessPoint(path = "/commits", type = GitHubCommit.class, requiresAccessToken = false)
	public List<GitHubCommit> getAllCommits() throws IllegalAccessException {
		List<GitHubCommit> commits = new ArrayList<GitHubCommit>();
		
		int i = 2;
		List<GitHubCommit> temp = getCommits(1);
		
		while (!temp.isEmpty()) {
			commits.addAll(temp);
			
			temp = getCommits(i);
			i++;
		}
		
		return commits;
	}

	@GitHubAccessPoint(path = "/commits", type = GitHubCommit.class, requiresAccessToken = false)
	public List<GitHubCommit> getAllCommits(GitHubUser author) throws IllegalAccessException {
		List<GitHubCommit> commits = new ArrayList<GitHubCommit>();
		
		int i = 2;
		List<GitHubCommit> temp = getCommits(1, author);
		
		while (!temp.isEmpty()) {
			commits.addAll(temp);
			
			temp = getCommits(i, author);
			i++;
		}
		
		return commits;
	}

	public GitHubCommit getCommit(String sha) throws IllegalAccessException {
		return new GitHubCommit(api, this, sha);
	}

	@GitHubAccessPoint(path = "/commits", type = GitHubCommit.class, requiresAccessToken = false)
	public List<GitHubCommit> getCommits(final int page) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("page", String.valueOf(page));
		params.put("per_page", String.valueOf(GitHub.ITEMS_PER_PAGE));
		
		GitHubObject commits = new GitHubObject(api, this, "/commits") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		
		JsonElement response = commits.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubCommit> list = new ArrayList<GitHubCommit>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubCommit commit = new GitHubCommit(api, this, object.get("sha").getAsString(), object);
	    	list.add(commit);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/commits", type = GitHubCommit.class, requiresAccessToken = false)
	public List<GitHubCommit> getCommits(final int page, final GitHubUser author) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("page", String.valueOf(page));
		params.put("per_page", String.valueOf(GitHub.ITEMS_PER_PAGE));
		params.put("author", author.getUsername());
		
		GitHubObject commits = new GitHubObject(api, this, "/commits") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		
		JsonElement response = commits.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubCommit> list = new ArrayList<GitHubCommit>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubCommit commit = new GitHubCommit(api, this, object.get("sha").getAsString(), object);
	    	list.add(commit);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/downloads", type = GitHubDownload.class, requiresAccessToken = false)
	public List<GitHubDownload> getDownloads() throws IllegalAccessException {
		GitHubObject downloads = new GitHubObject(api, this, "/downloads");
		JsonElement response = downloads.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubDownload> list = new ArrayList<GitHubDownload>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubDownload download = new GitHubDownload(api, this, object.get("id").getAsInt(), object);
	    	list.add(download);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues() throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final RepositoryFeature.State state) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", state.toString().toLowerCase());
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final GitHubLabel label) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		params.put("labels", label.getURLEncodedParameter());
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final RepositoryFeature.State state, final GitHubLabel label) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", state.toString().toLowerCase());
		params.put("labels", label.getURLEncodedParameter());
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final GitHubMilestone milestone) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		try {
			params.put("milestone", String.valueOf(milestone.getNumber()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final GitHubLabel label, final GitHubMilestone milestone) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		params.put("labels", label.getURLEncodedParameter());
		try {
			params.put("milestone", String.valueOf(milestone.getNumber()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/issues", type = GitHubIssue.class, requiresAccessToken = false)
	public List<GitHubIssue> getIssues(final RepositoryFeature.State state, final GitHubLabel label, final GitHubMilestone milestone) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", state.toString().toLowerCase());
		params.put("labels", label.getURLEncodedParameter());
		try {
			params.put("milestone", String.valueOf(milestone.getNumber()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		GitHubObject issues = new GitHubObject(api, this, "/issues") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubIssue> list = new ArrayList<GitHubIssue>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubIssue issue = new GitHubIssue(api, this, object.get("number").getAsInt(), object);
	    	list.add(issue);
	    }
		
		return list;
	}
	
	@GitHubAccessPoint(path = "/pulls", type = GitHubPullRequest.class, requiresAccessToken = false)
	public List<GitHubPullRequest> getPullRequests() throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		
		GitHubObject issues = new GitHubObject(api, this, "/pulls") {
			
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubPullRequest pr = new GitHubPullRequest(api, this, object.get("number").getAsInt(), object);
	    	list.add(pr);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/pulls", type = GitHubPullRequest.class, requiresAccessToken = false)
	public List<GitHubPullRequest> getPullRequests(final RepositoryFeature.State state) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", state.toString().toLowerCase());
		
		GitHubObject issues = new GitHubObject(api, this, "/pulls") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubPullRequest pr = new GitHubPullRequest(api, this, object.get("number").getAsInt(), object);
	    	list.add(pr);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/pulls", type = GitHubPullRequest.class, requiresAccessToken = false)
	public List<GitHubPullRequest> getPullRequests(final GitHubMilestone milestone) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", "all");
		try {
			params.put("milestone", String.valueOf(milestone.getNumber()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		GitHubObject issues = new GitHubObject(api, this, "/pulls") {
			
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubPullRequest pr = new GitHubPullRequest(api, this, object.get("number").getAsInt(), object);
	    	list.add(pr);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/pulls", type = GitHubPullRequest.class, requiresAccessToken = false)
	public List<GitHubPullRequest> getPullRequests(final RepositoryFeature.State state, final GitHubMilestone milestone) throws IllegalAccessException {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("state", state.toString().toLowerCase());
		try {
			params.put("milestone", String.valueOf(milestone.getNumber()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		GitHubObject issues = new GitHubObject(api, this, "/pulls") {
			
			@Override
			public Map<String, String> getParameters() {
				return params;
			}
			
		};
		JsonElement response = issues.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubPullRequest pr = new GitHubPullRequest(api, this, object.get("number").getAsInt(), object);
	    	list.add(pr);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/labels", type = GitHubLabel.class, requiresAccessToken = false)
	public List<GitHubLabel> getLabels() throws IllegalAccessException, UnsupportedEncodingException {
		GitHubObject labels = new GitHubObject(api, this, "/labels");
		JsonElement response = labels.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubLabel> list = new ArrayList<GitHubLabel>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubLabel issue = new GitHubLabel(api, this, object.get("name").getAsString(), object);
	    	list.add(issue);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "/milestones", type = GitHubMilestone.class, requiresAccessToken = false)
	public List<GitHubMilestone> getMilestones() throws IllegalAccessException, UnsupportedEncodingException {
		GitHubObject labels = new GitHubObject(api, this, "/milestones");
		JsonElement response = labels.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubMilestone> list = new ArrayList<GitHubMilestone>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubMilestone milestone = new GitHubMilestone(api, this, object.get("number").getAsInt(), object);
	    	list.add(milestone);
	    }
		
		return list;
	}

	@GitHubAccessPoint(path = "@owner", type = GitHubUser.class, requiresAccessToken = false)
	public GitHubUser getOwner() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "owner") ? null: new GitHubUser(api, response.get("owner").getAsJsonObject().get("login").getAsString(), response.get("owner").getAsJsonObject());
	}

	@GitHubAccessPoint(path = "@name", type = String.class, requiresAccessToken = false)
	public String getName() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "name") ? null: response.get("name").getAsString();
	}

	@GitHubAccessPoint(path = "@full_name", type = String.class, requiresAccessToken = false)
	public String getFullName() throws IllegalAccessException {
		if (fullname != null) return this.fullname;
		
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "full_name") ? null: response.get("full_name").getAsString();
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

	@GitHubAccessPoint(path = "@fork", type = Boolean.class, requiresAccessToken = false)
	public boolean isFork() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "fork") ? false: response.get("fork").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@has_issues", type = Boolean.class, requiresAccessToken = false)
	public boolean hasIssues() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "has_issues") ? false: response.get("has_issues").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@has_downloads", type = Boolean.class, requiresAccessToken = false)
	public boolean hasDownloads() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "has_downloads") ? false: response.get("has_downloads").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@has_wiki", type = Boolean.class, requiresAccessToken = false)
	public boolean hasWiki() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "has_wiki") ? false: response.get("has_wiki").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@has_pages", type = Boolean.class, requiresAccessToken = false)
	public boolean hasPages() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "has_pages") ? false: response.get("has_pages").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@has_projects", type = Boolean.class, requiresAccessToken = false)
	public boolean hasProjects() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "has_projects") ? false: response.get("has_projects").getAsBoolean();
	}

	@GitHubAccessPoint(path = "@homepage", type = String.class, requiresAccessToken = false)
	public String getWebsite() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "homepage") ? null: response.get("homepage").getAsString();
	}

	@GitHubAccessPoint(path = "@size", type = Integer.class, requiresAccessToken = false)
	public int getSize() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "size") ? null: response.get("size").getAsInt();
	}

	@GitHubAccessPoint(path = "@stargazers_count", type = Integer.class, requiresAccessToken = false)
	public int getStargazersAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "stargazers_count") ? null: response.get("stargazers_count").getAsInt();
	}

	@GitHubAccessPoint(path = "@watchers_count", type = Integer.class, requiresAccessToken = false)
	public int getWatchersAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "watchers_count") ? null: response.get("watchers_count").getAsInt();
	}

	@GitHubAccessPoint(path = "@forks_count", type = Integer.class, requiresAccessToken = false)
	public int getForksAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "forks_count") ? null: response.get("forks_count").getAsInt();
	}

	@GitHubAccessPoint(path = "@open_issues_count", type = Integer.class, requiresAccessToken = false)
	public int getOpenIssuesAmount() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "open_issues_count") ? null: response.get("open_issues_count").getAsInt();
	}

	@GitHubAccessPoint(path = "@subscribers_count", type = Integer.class, requiresAccessToken = false)
	public int getSubscribersAmount() throws IllegalAccessException {
		JsonElement element = getResponse(true);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "subscribers_count") ? null: response.get("subscribers_count").getAsInt();
	}

	@GitHubAccessPoint(path = "@language", type = GitHubLanguage.class, requiresAccessToken = false)
	public GitHubLanguage getDominantLanguage() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "language") ? null: new GitHubLanguage(api, this, response.get("language").getAsString());
	}

	@GitHubAccessPoint(path = "@default_branch", type = GitHubBranch.class, requiresAccessToken = false)
	public GitHubBranch getDefaultBranch() throws IllegalAccessException, UnsupportedEncodingException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "default_branch") ? null: new GitHubBranch(api, this, response.get("default_branch").getAsString());
	}

	public GitHubBranch getBranch(String name) throws IllegalAccessException, UnsupportedEncodingException {
		return new GitHubBranch(api, this, name);
	}

	public GitHubIssue getIssue(int number) throws IllegalAccessException {
		return new GitHubIssue(api, this, number);
	}

	public GitHubPullRequest getPullRequest(int number) {
		return new GitHubPullRequest(api, this, number);
	}

	public GitHubMilestone getMilestone(int number) {
		return new GitHubMilestone(api, this, number);
	}

	public GitHubLabel getLabel(String name) throws UnsupportedEncodingException {
		return new GitHubLabel(api, this, name);
	}

	@GitHubAccessPoint(path = "@pushed_at", type = Date.class, requiresAccessToken = false)
	public Date getLastPushedDate() throws IllegalAccessException {
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();

		return isInvalid(response, "pushed_at") ? null: GitHubDate.parse(response.get("pushed_at").getAsString());
	}
	
	@GitHubAccessPoint(path = "/git/refs", type = GitHubReference.class, requiresAccessToken = false)
	public List<GitHubReference> getReferences() throws IllegalAccessException {
		GitHubObject refs = new GitHubObject(api, this, "/git/refs");
		JsonElement response = refs.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubReference> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubReference ref = new GitHubReference(api, this, object.get("ref").getAsString(), object);
	    	list.add(ref);
	    }
		
		return list;
	}
	
	@GitHubAccessPoint(path = "/tags", type = GitHubTag.class, requiresAccessToken = false)
	public List<GitHubTag> getTags() throws IllegalAccessException {
		GitHubObject tags = new GitHubObject(api, this, "/tags");
		JsonElement response = tags.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubTag> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubTag tag = new GitHubTag(api, this, object);
	    	list.add(tag);
	    }
		
		return list;
	}
	
	@GitHubAccessPoint(path = "/comments", type = GitHubComment.class, requiresAccessToken = false)
	public List<GitHubComment> getCommitComments() throws IllegalAccessException {
		GitHubObject comments = new GitHubObject(api, this, "/comments");
		JsonElement response = comments.getResponse(true);
		
		if (response == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		
		List<GitHubComment> list = new ArrayList<>();
		JsonArray array = response.getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();
	    	
	    	GitHubComment comment = new GitHubComment(api, this, object.get("id").getAsInt(), object);
	    	list.add(comment);
	    }
		
		return list;
	}
	
	@GitHubAccessPoint(path = "@parent", type = GitHubRepository.class, requiresAccessToken = false)
	public GitHubRepository getForkParent() throws IllegalAccessException {
		if (!isFork()) return null;
		
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "parent") ? null: new GitHubRepository(api, response.get("parent").getAsJsonObject().get("full_name").getAsString(), response.get("parent").getAsJsonObject());
	}
	
	@GitHubAccessPoint(path = "@source", type = GitHubRepository.class, requiresAccessToken = false)
	public GitHubRepository getForkSource() throws IllegalAccessException {
		if (!isFork()) return null;
		
		JsonElement element = getResponse(false);
		
		if (element == null) {
			throw new IllegalAccessException("Could not connect to '" + getURL() + "'");
		}
		JsonObject response = element.getAsJsonObject();
		
		return isInvalid(response, "source") ? null: new GitHubRepository(api, response.get("source").getAsJsonObject().get("full_name").getAsString(), response.get("source").getAsJsonObject());
	}
}
