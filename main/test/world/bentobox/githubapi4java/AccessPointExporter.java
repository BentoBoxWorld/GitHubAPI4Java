package world.bentobox.githubapi4java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class AccessPointExporter {
	
	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
		
		AccessPointVisualizer.run(false);
		
		System.out.println("Exporting files...");
		
		try {
			FileWriter writer = new FileWriter("E:\\Projects\\GitHub\\TheBusyBiscuit.github.io\\docs\\GitHubWebAPI4Java\\assets\\api.json");
			
			JsonArray settings = new JsonArray();
			
			List<String> keys = new ArrayList<String>(AccessPointVisualizer.categories.keySet());
			
			Collections.sort(keys, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if (!o1.split(" | ")[2].equals("User") && o2.split(" | ")[2].equals("User")) {
						return 1;
					}
					else if (!o1.split(" | ")[2].equals("Repository") && o2.split(" | ")[2].equals("Repository")) {
						return 1;
					}
					else if (o1.split(" | ")[2].equals("User") && !o2.split(" | ")[2].equals("User")) {
						return -1;
					}
					else if (o1.split(" | ")[2].equals("Repository") && !o2.split(" | ")[2].equals("Repository")) {
						return -1;
					}
					else {
						return 0;
					}
				}
			});
			
			int i = 0;
			for (String key: keys) {
				List<String> values = AccessPointVisualizer.categories.get(key);
				JsonObject category = new JsonObject();
				category.addProperty("name", key.split(" | ")[2]);
				category.addProperty("file", i + ".txt");
				category.addProperty("url", key.split(" | ")[0]);
				
				FileWriter text = new FileWriter("E:\\Projects\\GitHub\\TheBusyBiscuit.github.io\\docs\\GitHubWebAPI4Java\\assets\\" + i + ".txt");
				text.write(AccessPointVisualizer.data.get(key));
				i++;
				
				JsonArray children = new JsonArray();
				
				for (String child: values) {
					JsonObject obj = new JsonObject();

					obj.addProperty("type", key.split(" | ")[2]);
					obj.addProperty("key", toKey(child.split(" | ")[0].replace(key.split(" | ")[0], "")));
					obj.addProperty("url", child.split(" | ")[0].replace(key.split(" | ")[0], ""));
					obj.addProperty("file", i + ".txt");
					
					FileWriter text2 = new FileWriter("E:\\Projects\\GitHub\\TheBusyBiscuit.github.io\\docs\\GitHubWebAPI4Java\\assets\\" + i + ".txt");
					text2.write(AccessPointVisualizer.data.get(child));

					text2.flush();
					text2.close();
					
					children.add(obj);
					i++;
				}
				
				category.add("children", children);
				
				text.flush();
				text.close();
				
				settings.add(category);
			}
			
			gson.toJson(settings, writer);
			
			writer.flush();
			writer.close();
		}
		catch(Exception x) {
			x.printStackTrace();
			System.exit(0);
		}
	}

	private static String toKey(String url) {
		StringBuilder builder = new StringBuilder();
		
		for (String segment: url.split("/")) {
			if (segment.equals(""));
			else if (Pattern.matches("[0-9]+", segment)) {
				builder.append("/");
				builder.append("[##]");
			}
			else if (segment.length() == 40 && Pattern.matches("[0-9a-f]+", segment)) {
				builder.append("/");
				builder.append("[##]");
			}
			else {
				builder.append("/");
				builder.append(segment);
			}
		}
		
		return builder.toString();
	}

}
