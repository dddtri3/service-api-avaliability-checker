package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dddtri.qa.api.data.AbstractApiData;
import com.dddtri.qa.api.data.TestData;

/**
 * 
 * @author daniel.shih
 *
 */
public class LogGenImpl implements LogGen {

	private static Logger logger = Logger.getLogger(LogGenImpl.class);
	
	public static final String LOG_DIR = "./log/";
	
	public static final String PASS_FOLDER_NAME = "pass";
	
	public static final String FAIL_FOLDER_NAME = "fail";

	private List<TestData> datas;
	public LogGenImpl(List<TestData> datas) {
		this.datas = datas;

	}
	@Override
	public void run() {
		long currentTime = System.currentTimeMillis();
		//generate report
		Map<String, StringBuilder> successLogMap = new HashMap<String, StringBuilder>();
		Map<String, StringBuilder> failLogMap = new HashMap<String, StringBuilder>();
		for (TestData data : datas) {
			AbstractApiData testData = (AbstractApiData) data;
			ResultMap resultMap = (ResultMap) testData.getMetadat(MisSupportApiData.OUTPUT_RESULT_MAP);

			if ("ok".equals(resultMap.getStatus().toLowerCase().trim())) {
				if (! successLogMap.containsKey(testData.getBaseUrl())) {
					successLogMap.put(testData.getBaseUrl(), new StringBuilder());
				}
				successLogMap.get(testData.getBaseUrl()).append(resultMap.getTestcaseName() + "," + resultMap.getStatus() + "\n");
				//writeToPassLog(currentTime, successLog, testData.getBaseUrl());
			} else {
				if (! failLogMap.containsKey(testData.getBaseUrl())) {
					failLogMap.put(testData.getBaseUrl(), new StringBuilder());
				}
				failLogMap.get(testData.getBaseUrl()).append(resultMap.getTestcaseName() + "," + resultMap.getStatus() + "\n");
				//writeToFailLog(currentTime, badLog, testData.getBaseUrl());
			}
		}
		
		for (Entry<String, StringBuilder> row : successLogMap.entrySet()) {
			this.writeToPassLog(currentTime, row.getValue(), row.getKey());
		}
		
		for (Entry<String, StringBuilder> row : failLogMap.entrySet()) {
			this.writeToFailLog(currentTime, row.getValue(), row.getKey());
		}

	}
	private void writeToPassLog(long currentTime, StringBuilder successLog, String baseUrl) {
		File successLogFile;
		String filePath = LOG_DIR + "/" +  PASS_FOLDER_NAME + "/" + URLEncoder.encode((
				baseUrl + "_" + currentTime + ".log"));
		try {
			successLogFile = new File(filePath);
			successLogFile.getAbsolutePath();
			if (successLogFile.isFile()) {
				FileUtils.writeLines(successLogFile, Arrays.asList(successLog.toString()));
			} else {
				FileUtils.writeStringToFile(successLogFile, successLog.toString());	
			}
			
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Unable to create success log[%s]", filePath) , e);
		} catch (IOException e) {
			logger.error(String.format("Unable to create success log[%s]", filePath) , e);
		}
	}
	private void writeToFailLog(long currentTime, StringBuilder badLog, String baseUrl) {
		File failLogFile;
		String filePath = LOG_DIR + "/" +  FAIL_FOLDER_NAME + "/" + URLEncoder.encode((
				baseUrl + "_" + currentTime + ".log"));
		try {
			failLogFile = new File(filePath);
			failLogFile.getAbsolutePath();
			FileUtils.writeStringToFile(failLogFile, badLog.toString());
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Unable to create fail log[%s]", filePath) , e);
		} catch (IOException e) {
			logger.error(String.format("Unable to create fail log[%s]", filePath) , e);
		}
	}
}
