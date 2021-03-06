package world.bentobox.githubapi4java.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GitHubAccessPoint {
	
	String path();
	Class<?> type();
	boolean requiresAccessToken();
	
}
