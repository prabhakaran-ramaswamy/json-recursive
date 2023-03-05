package com.example.jsonrecursive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@SpringBootApplication
public class JsonrecursiveApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(JsonrecursiveApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> keyMap = new HashMap<String, String>();
		JsonNode jsonNode = mapper.readTree(readFile());
		conertToMap(jsonNode, keyMap);
		System.out.println(keyMap);
	}

	public String readFile() {
		String data = "";
		try {
			File myObj = ResourceUtils.getFile("classpath:data.json");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data += myReader.nextLine();
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}

	private void conertToMap(JsonNode jsonNode, Map<String, String> keyMap) {

		if (jsonNode.isObject()) {
			Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();
			fields.forEachRemaining(field -> {
				JsonNode value = field.getValue();
				if (value.isValueNode()) {
					keyMap.put(field.getKey(), value.asText());
				} else {
					conertToMap((JsonNode) value, keyMap);
				}
			});
		} else if (jsonNode.isArray()) {
			ArrayNode arrayField = (ArrayNode) jsonNode;
			arrayField.forEach(node -> {
				conertToMap(node, keyMap);
			});
		} else if (jsonNode.isValueNode()) {
			Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();
				keyMap.put(entry.getKey(), entry.getValue().asText());
			}
		}
	}

}
