package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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

			if ("ok".equals(resultMap.getStatus().toLowerCase().trim())) {
				//successLog.//TODO: get testcase name
			}
		}

		if (! successLog.toString().isEmpty()) {
			File successLogFile;
			String filePath = "./log/pass/" + URLEncoder.encode((
					((AbstractApiData) datas.get(0))).getBaseUrl() + "_" + currentTime + ".log");
			try {
				successLogFile = new File(filePath, "UTF-8");
				FileUtils.writeStringToFile(successLogFile, successLog.toString());
				FileUtils.forceMkdir(successLogFile);	
			} catch (UnsupportedEncodingException e) {
				logger.error(String.format("Unable to create success log[%s]", filePath) , e);
			} catch (IOException e) {
				logger.error(String.format("Unable to create success log[%s]", filePath) , e);
			}

		}

		if (! badLog.toString().isEmpty()) {
			String filePath = "./fail/pass/" + URLEncoder.encode((
					((AbstractApiData) datas.get(0))).getBaseUrl() + "_" + currentTime + ".log");
			try {
				File badLogFile = new File(filePath, "UTF-8");
				FileUtils.writeStringToFile(badLogFile, successLog.toString());
				FileUtils.forceMkdir(badLogFile);
			} catch (UnsupportedEncodingException e) {
				logger.error(String.format("Unable to create success log[%s]", filePath) , e);
			} catch (IOException e) {
				logger.error(String.format("Unable to create success log[%s]", filePath) , e);
			}
		}	
	}

}
