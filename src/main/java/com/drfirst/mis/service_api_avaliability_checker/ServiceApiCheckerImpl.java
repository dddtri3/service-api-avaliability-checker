package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dddtri.qa.api.ClientRunner;
import com.dddtri.qa.api.MockClientRunner;
import com.dddtri.qa.api.data.AbstractTestData;
import com.dddtri.qa.api.data.TestData;
import com.dddtri.qa.api.data.TestDataFactory;

/**
 * 
 * @author daniel.shih
 *
 */
public class ServiceApiCheckerImpl implements ServiceApiChecker {

	private static Logger logger = Logger.getLogger(ServiceApiCheckerImpl.class);
	private int concurrentRunThread =2;
	private final String baseDir= "D:\\instanceTest";

	@Override
	public void run() {
		File srcListFile = new File(getBaseDir(),"host.csv");
		
		srcListFile.
		
		List<HostEntry> hostsList = new ArrayList<HostEntry>();
		//TODO: dicover xml files
		List<String> xmls =  new ArrayList<String>();
		File dir = null;
		for (File xmlFile : dir.listFiles()) {
				try {
					xmls.add(FileUtils.readFileToString(xmlFile));
					logger.info("discovered [%s] amount of xml apis..");
				} catch (IOException e) {
					logger.error(String.format("skipped recognizing xml[%s] dued to following stacktrace", xmlFile.getAbsoluteFile()), e);
				}	
		}
		logger.info("discovered [%s] amount of xml apis..");

		for (HostEntry hostEntry : hostsList) {

			// data preparation
			List<TestData> datas = new ArrayList<TestData>();
			for (String xml : xmls) {

				Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
				List<String> xmlList = new ArrayList<String>();
				try {
					xmlList.add(URLEncoder.encode(xml, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(String.format("skipped creatin data for xml[%s] dued to following stacktrace", xml), e);
				}
				paraMap.put(MisSupportApiData.INPUT_XML_NAME, xmlList);
				paraMap.put(MisSupportApiData.INPUT_MAC_NAME, xmlList);
				paraMap.put(MisSupportApiData.INPUT_TIME_NAME, xmlList);
				paraMap.put(MisSupportApiData.INPUT_AUTH_NAME, xmlList);

				datas.add(TestDataFactory.create(MisSupportApiData.class, hostEntry.getHost() + MisSupportApiData.URI));	

			}
			logger.info(String.format("prepared [%s] amount of apis. for host[%s]...", datas.size(), hostEntry.getHost()));


			// execute
			long currentTime = System.currentTimeMillis();
			ClientRunner runner = new MockClientRunner();
			runner.newFixedThreadPool(concurrentRunThread);
			try {
				runner.run(datas.toArray(new TestData[]{}));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			logger.info(String.format("check run completed that took [%s] amount of time...", System.currentTimeMillis() -  currentTime));
			
			//generate report
			for (TestData data : datas) {
				AbstractTestData testData = (AbstractTestData) data;
				//testData.getMetadat(key)

				//TODO: distinguish bad & good result & output
			}
		}

	}
	private String getBaseDir(){
		return baseDir;
	}

}
