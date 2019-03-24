package world.bentobox.githubapi4java.extra;

import world.bentobox.githubapi4java.GitHubCommit;
import world.bentobox.githubapi4java.GitHubFile;
import world.bentobox.githubapi4java.GitHubRepository;
import world.bentobox.githubapi4java.GitHubUser;

import java.util.ArrayList;
import java.util.List;

public class RepositorySnapshot {
	
	private GitHubCommit commit;
	
	public RepositorySnapshot(GitHubCommit commit) {
		this.commit = commit;
	}
	
	public GitHubRepository getRepository() {
		return this.commit.getRepository();
	}
	
	public GitHubCommit getCommit() {
		return this.commit;
	}
	
	public String getCommitMessage() throws IllegalAccessException {
		return this.commit.getMessage();
	}
	
	public GitHubUser getAuthor() throws IllegalAccessException {
		return this.commit.getAuthor();
	}
	
	public List<String> getFiles() throws IllegalAccessException {
		List<String> files = new ArrayList<String>();
		
		for (GitHubFile file: this.commit.getFileTree().getFiles()) {
			files.add(file.getFile());
		}
		
		return files;
	}

}
