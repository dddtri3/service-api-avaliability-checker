package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.dddtri.qa.api.data.TestData;
import com.dddtri.qa.api.data.TestDataFactory;
import com.dddtri.qa.tc.junit.AbstractJunit4TestBase;

public class LogGenImplTest extends AbstractJunit4TestBase {

	@Before
	public void before() throws IOException {
		FileUtils.deleteDirectory(new File(LogGenImpl.LOG_DIR));
	}
	
	
	@Test
	public void testRun() {
		
		//input preparation
		List<TestData> datas = new ArrayList<TestData>();
		MisSupportApiData goodData = createDefaultApiData();
		datas.add(goodData);
		ResultMap resultMap = new ResultMap();
		resultMap.setStatus(" Ok ");
		resultMap.setTestcaseName("send_patient");
		goodData.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		
		MisSupportApiData goodData2 = createDefaultApiData();
		datas.add(goodData2);
		resultMap = new ResultMap();
		resultMap.setStatus(" ok ");
		resultMap.setTestcaseName("send_patient");
		goodData2.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		
		MisSupportApiData badData = createDefaultApiData();
		datas.add(badData);
		resultMap = new ResultMap();
		resultMap.setStatus("no ok");
		resultMap.setTestcaseName("send_patient");
		badData.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		
		
		MisSupportApiData badData2 = createDefaultApiData();
		datas.add(badData2);
		resultMap = new ResultMap();
		resultMap.setStatus("no ok");
		resultMap.setTestcaseName("send_patient2");
		badData2.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		
		
		MisSupportApiData anotherHostBadApiData = createDefaultApiData();
		anotherHostBadApiData.setBaseUrl("https://www.anotherhosturl.com");
		datas.add(anotherHostBadApiData);
		resultMap = new ResultMap();
		resultMap.setStatus("no ok");
		resultMap.setTestcaseName("send_patient2");
		anotherHostBadApiData.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		
		//execute
		LogGen runner = new LogGenImpl(datas);
		runner.run();
		
		//verify
		File passFolder = new File(LogGenImpl.LOG_DIR + "/" + LogGenImpl.PASS_FOLDER_NAME);
		Assert.assertTrue(passFolder.isDirectory());
		Assert.assertEquals(1, passFolder.listFiles().length);
		
		
		File failFolder = new File(LogGenImpl.LOG_DIR + "/" + LogGenImpl.FAIL_FOLDER_NAME);
		Assert.assertTrue(failFolder.isDirectory());
		Assert.assertEquals(2, failFolder.listFiles().length);
	}

	private static MisSupportApiData createDefaultApiData() {
		MisSupportApiData apiData;
		String baseUrl = "http://test1234.com";
		Map<String, List<String>> paras = new HashMap<String, List<String>>();
		paras.put(MisSupportApiData.INPUT_AUTH_NAME, Arrays.asList("y"));
		paras.put(MisSupportApiData.INPUT_MAC_NAME, Arrays.asList("mac"));
		paras.put(MisSupportApiData.INPUT_TIME_NAME, Arrays.asList("time"));
		paras.put(MisSupportApiData.INPUT_XML_NAME, Arrays.asList("xml"));
		
		apiData = TestDataFactory.create(MisSupportApiData.class, baseUrl, paras);
		return apiData;
	}
}
