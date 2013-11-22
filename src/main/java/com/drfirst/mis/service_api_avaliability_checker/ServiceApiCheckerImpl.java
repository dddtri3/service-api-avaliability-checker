package com.drfirst.mis.service_api_avaliability_checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dddtri.qa.api.ClientRunner;
import com.dddtri.qa.api.MockClientRunner;
import com.dddtri.qa.api.data.TestData;
import com.dddtri.qa.api.data.TestDataFactory;

/**
 * 
 * @author daniel.shih
 *
 */
public class ServiceApiCheckerImpl implements ServiceApiChecker {
	
	public static void main(String args[]) {
		ServiceApiChecker test = new ServiceApiCheckerImpl();
		test.run();
	}
	private static final String KEY_API_LIST = "API_LIST";
	private static final String KEY_XML_SOURCE_DIR = "XML_SOURCE_DIR";
	private static Properties props;
	private static Logger logger = Logger.getLogger(ServiceApiCheckerImpl.class);
	private int concurrentRunThread =2;

	private final String FILE_TEXT_EXT = ".xml";

	@Override
	public void run() {
        props = new Properties();
	    try {
			props.load(new FileInputStream(new File("").getAbsoluteFile() + "/resources/service-api-avaliability-checker.properties"));
			Iterator<Object> it = props.keySet().iterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String XML_SOURCE_DIR = props.getProperty(KEY_XML_SOURCE_DIR);
		String API_LIST = props.getProperty(KEY_API_LIST);
	    

		List<HostEntry> hostsList =this.getParaFromFile();
		List<String> testcaseNames = new ArrayList<String>();
		List<String> xmlContents =  new ArrayList<String>();
		
		for(File xmlFile : new File(XML_SOURCE_DIR).listFiles(new FileNameFilter(FILE_TEXT_EXT))){
		try {
			xmlContents.add(FileUtils.readFileToString(xmlFile));
			testcaseNames.add(xmlFile.getName());
			logger.info("discovered [%s] amount of xml apis..");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(String.format("skipped recognizing xml[%s] dued to following stacktrace", testcaseNames), e);
		}

		}
		logger.info("discovered [%s] amount of xml apis..");
		
		List<TestData> datas = new ArrayList<TestData>();

		for (HostEntry hostEntry : hostsList) {
			// data preparation
			int i=0;
			for (String xml : xmlContents) {

				Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
				List<String> xmlList = new ArrayList<String>();
				try {
					xmlList.add(URLEncoder.encode(xml, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(String.format("skipped creatin data for xml[%s] dued to following stacktrace", xml), e);
				}
				
				
				List<String> macList = new ArrayList<String>();
				try {
					macList.add(URLEncoder.encode(hostEntry.getMac(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(String.format("skipped creatin data for mac[%s] dued to following stacktrace", hostEntry.getMac()), e);
				}
				
				List<String> timeList = new ArrayList<String>();
				try {
					timeList.add(URLEncoder.encode(hostEntry.getTime(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(String.format("skipped creatin data for time[%s] dued to following stacktrace", hostEntry.getTime()), e);
				}
				
				List<String> userList = new ArrayList<String>();
				try {
					userList.add(URLEncoder.encode(hostEntry.getAuthUser(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(String.format("skipped creatin data for authUser[%s] dued to following stacktrace", hostEntry.getAuthUser()), e);
				}
				
				
				paraMap.put(MisSupportApiData.INPUT_XML_NAME, xmlList);
				paraMap.put(MisSupportApiData.INPUT_MAC_NAME, macList);
				paraMap.put(MisSupportApiData.INPUT_TIME_NAME, timeList);
				paraMap.put(MisSupportApiData.INPUT_AUTH_NAME, userList);

				MisSupportApiData data = TestDataFactory.create(MisSupportApiData.class,hostEntry.getHost(),paraMap);
				data.putMetadata(MisSupportApiData.OUTPUT_TESTCASE_NAME,testcaseNames.get(i) );
				datas.add(data);
				
				i++;
			}
			logger.info(String.format("prepared [%s] amount of apis. for host[%s]...", datas.size(), hostEntry.getHost()));


			// execute
			long currentTime = System.currentTimeMillis();
			ClientRunner runner = new MockClientRunner();
			runner.newFixedThreadPool(concurrentRunThread);
			try {
				runner.run(datas.toArray(new TestData[]{}));
			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info(String.format("check run completed that took [%s] amount of time...", System.currentTimeMillis() -  currentTime));
			
			//generate report
			LogGen logGen = new LogGenImpl(datas);
			logGen.run();
			logger.info(String.format("report gen for instance [%s]. completed ...", hostEntry.getHost()));

		}
		logger.info(String.format("there are %s datas in the Data array!", datas.size()));

	}


	private List<HostEntry> getParaFromFile(){
		BufferedReader buff = null;
	
		try {
			buff = new BufferedReader(new FileReader(this.props.getProperty(KEY_API_LIST)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String csvString;

		String pattern = "http.+?,.+?,[ynYN]?,.+?";
		Pattern p = Pattern.compile(pattern);

		List<HostEntry> hostsList = new ArrayList<HostEntry>();

		try {
			while ((csvString=buff.readLine()) != null){
				csvString=csvString.trim().toLowerCase();
				Matcher m = p.matcher(csvString);	
				if(!m.find()){
					continue;
				}
				
				String[]parsed=csvString.split(",");
				HostEntryImpl entry = new HostEntryImpl();
				entry.setHost(parsed[0]);
				entry.setTime(parsed[1]);
				entry.setAuthUser(parsed[2]);
				entry.setMac(parsed[3]);
				logger.info("set host "+ entry.getHost());
				hostsList.add(entry);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		try {
			buff.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return hostsList;
	}
	
	
	
	
}
