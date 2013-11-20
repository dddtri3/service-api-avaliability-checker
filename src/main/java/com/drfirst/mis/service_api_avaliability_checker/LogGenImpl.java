package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.dddtri.qa.api.data.AbstractApiData;
import com.dddtri.qa.api.data.TestData;

public class LogGenImpl implements LogGen {

	private List<TestData> datas;
	public LogGenImpl(List<TestData> datas) {
		this.datas = datas;
		
	}
	@Override
	public void run() {
		long currentTime = System.currentTimeMillis();
		//generate report
		StringBuilder successLog = new StringBuilder();
		StringBuilder badLog = new StringBuilder();
		for (TestData data : datas) {
			long time = System.currentTimeMillis();
			AbstractApiData testData = (AbstractApiData) data;
			ResultMap resultMap = (ResultMap) testData.getMetadat(MisSupportApiData.OUTPUT_RESULT_MAP);
			
			//TODO: distinguish bad & good result & output
		}
		
		if (! successLog.toString().isEmpty()) {
			File successLogFile = new File("./log/pass/" + URLEncoder.encode((
					((AbstractApiData) datas.get(0))).getBaseUrl() + "_" + currentTime + ".log", "UTF-8"));
			FileUtils.writeStringToFile(successLogFile, successLog.toString());
			FileUtils.forceMkdir(successLogFile);				
		}
		
		if (! badLog.toString().isEmpty()) {
			File badLogFile = new File("./log/fail/" + URLEncoder.encode((
					((AbstractApiData) datas.get(0))).getBaseUrl() + "_" + currentTime + ".log", "UTF-8"));
			FileUtils.writeStringToFile(badLogFile, successLog.toString());
			FileUtils.forceMkdir(badLogFile);				
		}	}

}
