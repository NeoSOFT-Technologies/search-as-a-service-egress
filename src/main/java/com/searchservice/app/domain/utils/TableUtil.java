package com.searchservice.app.domain.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.searchservice.app.rest.errors.OperationIncompleteException;

public class TableUtil {
	
	private TableUtil() {}
	
	@Value("${table-schema-attributes.delete-file-path}")
	static String deleteSchemaAttributesFilePath;
	
	// Soft Delete Table Schema Info Retrieval
	public static List<String> readSchemaInfoFromSchemaDeleteManager(
			int clientId, String tableName) {
		List<String> deletedSchemaAttributes = new ArrayList<>();
		
		File schemaSoftDeleteFile = new File(deleteSchemaAttributesFilePath);
		try (BufferedReader br = new BufferedReader(new FileReader(schemaSoftDeleteFile))) {
			int lineNumber = 0;
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split("\\s+");				
					if (currentRecordData[0].equalsIgnoreCase(String.valueOf(clientId))
							&&	currentRecordData[1].equalsIgnoreCase(String.valueOf(tableName))) {
						deletedSchemaAttributes.add(currentRecordData[4]);
					}
				}
				lineNumber++;
			}
		} catch (Exception e) {
			throw new OperationIncompleteException(500, "Soft Delete SchemaInfo could not be retrieved");
		}
		
		return deletedSchemaAttributes;
	}
	
}
