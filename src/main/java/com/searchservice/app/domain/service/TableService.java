package com.searchservice.app.domain.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.dto.table.TableSchemav2.TableSchemav2Data;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SolrUtil;
import com.searchservice.app.domain.utils.TableSchemaParser;
import com.searchservice.app.domain.utils.TableUtil;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;

@Service
@Transactional
public class TableService {

	private final Logger logger = LoggerFactory.getLogger(TableService.class); 
	
	private static final String SIMPLE_DATE_FORMATTER = "dd-M-yyyy hh:mm:ss";
	
	@Value("${base-solr-url}")
	private String solrURL;
	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;
	
	// Table
	@Value("${table-schema-attributes.delete-file-path}")
	String deleteSchemaAttributesFilePath;
	SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMATTER);
	
	@Autowired
	SolrAPIAdapter solrAPIAdapter = new SolrAPIAdapter();
	
	//GetCurrentSchemaUtil getCurrentSchemaUtil = new GetCurrentSchemaUtil();
	
	public List<String> getCurrentTableSchema(String tableName, int clientId) {
		
		logger.info("inside getCurrentTableSchema ########");

		GetCurrentSchemaUtil getCurrentSchemaUtil = extracted(tableName, clientId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchemaUtil.get();
		String responseString = response.getResponseString();
		List<String> currentSchemaColumnNames = getCurrentSchemaUtil.getCurrentSchemaColumns(responseString);

		return currentSchemaColumnNames;
	}
	
	
	public List<SolrDocument> syncTableDocumentsWithSoftDeletedSchema(
			SolrDocumentList docs, List<SolrDocument> solrDocs, List<String> validColumns) {
		
		logger.info("inside syncTable..... meth #####");
		
		logger.info("SolrDocsList >>>>> {}", docs);
		
		
		docs.forEach(
				d -> {
					// logic
					List<String> columnsNames = (List<String>)d.getFieldNames();
					logger.info("fieldNames >>>>>>>>>>> {}", columnsNames);
					
					// Return only valid schema columns
					columnsNames.forEach(col -> {
						if(validColumns.contains(col))
							solrDocs.add(d);
					});
				});
		logger.info("after forEach $$$$$");
				
		
		return new ArrayList<>();
	}
	

	private GetCurrentSchemaUtil extracted(String tableName, int clientId) {
		GetCurrentSchemaUtil getCurrentSchemaUtil = new GetCurrentSchemaUtil();

		getCurrentSchemaUtil.setBaseIngressMicroserviceUrl(baseIngressMicroserviceUrl + getTableUrl);
		getCurrentSchemaUtil.setTableName(tableName);
		getCurrentSchemaUtil.setClientId(clientId);
		return getCurrentSchemaUtil;
	}
	
	
	///////////
	
	public TableSchemav2 compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(
			String tableName, int clientId) {
		// GET tableSchema at solr cloud
		TableSchemav2 tableSchema = getTableSchema(tableName + "_" + clientId);
		
		TableSchemav2Data data= new TableSchemav2Data();
		data.setTableName(tableName);
		
		
		List<SchemaField> schemaAttributesCloud = tableSchema.getData().getColumns();
		
		// testing
		logger.info("all good till here ######");
		
		// READ from SchemaDeleteRecord.txt and exclude the deleted attributes
		List<String> deletedSchemaAttributesNames = TableUtil.readSchemaInfoFromSchemaDeleteManager(
				clientId, tableName);

		// Prepare the final tableSchema to return
		List<SchemaField> schemaAttributesFinal = new ArrayList<>();
		List<String> schemaAttributesToSkipNames = new ArrayList<>();
		// Note down schemaAttributes to skip
		for(SchemaField dto: schemaAttributesCloud) {
			if(!deletedSchemaAttributesNames.contains(dto.getName())) {
				schemaAttributesFinal.add(dto);	
			}
			schemaAttributesToSkipNames.add(dto.getName());
		}
		data.setColumns(schemaAttributesFinal);
		tableSchema.setData(data);
		
		return tableSchema;
	}
	
	
	public TableSchemav2 getTableSchema(String tableName) {
		logger.info("Getting table schema");

		SolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		TableSchemav2 tableSchemaResponseDTO = new TableSchemav2();
		TableSchemav2Data data= new TableSchemav2Data();
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClientActive);
			logger.info("Get request has been processed. Setting status code = 200");
			tableSchemaResponseDTO.setStatusCode(200);

			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			List<SchemaField> solrSchemaFieldDTOs = new ArrayList<>();
			logger.info("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {
				
				// Prepare the SolrFieldDTO
				SchemaField solrFieldDTO = new SchemaField();
				solrFieldDTO.setName((String) f.get("name"));

				// Parse Field Type Object(String) to Enum
				String solrFieldType = SchemaFieldType.fromSolrFieldTypeToStandardDataType(
						(String) f.get("type"));

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParser.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs.add(solrFieldDTO);
				schemaFieldIdx++;
			}
			logger.info("Total fields stored in attributes array: {}", schemaFieldIdx);
			
			// prepare response dto
			data.setTableName(tableName.split("_")[0]);
			data.setColumns(solrSchemaFieldDTOs);
			tableSchemaResponseDTO.setData(data);
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (Exception e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(e.getMessage(), tableName);
			logger.info(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		
		return tableSchemaResponseDTO;
	}
}
