package com.drfirst.mis.service_api_avaliability_checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.dddtri.qa.api.data.TestData;
import com.dddtri.qa.api.data.TestDataFactory;
import com.dddtri.qa.tc.junit.AbstractJunit4TestBase;

public class LogGenImplTest extends AbstractJunit4TestBase {

	@Test
	public void testRun() {
		List<TestData> datas = new ArrayList<TestData>();
		String baseUrl = "http://test1234.com";
		Map<String, List<String>> paras = new HashMap<String, List<String>>();
		paras.put(MisSupportApiData.INPUT_AUTH_NAME, Arrays.asList("y"));
		paras.put(MisSupportApiData.INPUT_MAC_NAME, Arrays.asList("mac"));
		paras.put(MisSupportApiData.INPUT_TIME_NAME, Arrays.asList("time"));
		paras.put(MisSupportApiData.INPUT_XML_NAME, Arrays.asList("xml"));
		
		MisSupportApiData apiData = TestDataFactory.create(MisSupportApiData.class, baseUrl, paras);
		datas.add(apiData);
		
		apiData.putMetadata(MisSupportApiData.OUTPUT_TESTCASE_NAME, "send_patient");
		
		ResultMap resultMap = new ResultMap();
		resultMap.setStatus("ok");
		resultMap.setTestcaseName("send_patient");
		apiData.putMetadata(MisSupportApiData.OUTPUT_RESULT_MAP, resultMap);
		LogGen runner = new LogGenImpl(datas);
		runner.run();
	}
}
