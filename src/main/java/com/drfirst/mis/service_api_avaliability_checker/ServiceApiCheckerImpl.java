package com.drfirst.mis.service_api_avaliability_checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dddtri.qa.api.ClientRunner;
import com.dddtri.qa.api.MockClientRunner;
import com.dddtri.qa.api.data.AbstractApiData;
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
	private final String entryDir= "D:\\instanceTest\\HostEntry";
	private final String xmlDir= "D:\\instanceTest\\xmlPost";
	private final String FILE_TEXT_EXT = ".xml";

	@Override
	public void run() {
		List<HostEntry> hostsList =this.getParaFromFile();
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
			LogGen logGen = new LogGenImpl(datas);
			logGen.run();
			logger.info(String.format("report gen for instance [%s]. completed ...", hostEntry.getHost()));

		}

	}


	private List<HostEntry> getParaFromFile(){
		BufferedReader buff = null;
	
		try {
			buff = new BufferedReader(new FileReader("D:\\instanceTest\\host.csv"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String csvString;

		String pattern = "http://.+?,.+?,[yn],.+";
		Pattern p = Pattern.compile(pattern);

		List<HostEntry> hostsList = new ArrayList<HostEntry>();

		try {
			while ((csvString = buff.readLine().trim().toLowerCase()) != null){
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
	
	
	public List<String> listFile() {
		List<String> xmlList = new ArrayList<String>();
		File dir = new File(xmlDir);
 
		if(dir.isDirectory()==false){
			throw new RuntimeException("The appointed xml source is not a directory.");
		}
 
		// list out all the file name and filter by the extension
		String[] xmlArray = dir.list(new FileNameFilter(FILE_TEXT_EXT));
		
		for(String xmlFileName: xmlArray){
		xmlList.add(xmlFileName);
		}
		
		if (xmlList.size() == 0) {
			throw new RuntimeException("The appointed xml source doesn't contain any xml file!");
		}
		return xmlList;
	}
	
	
	
}
