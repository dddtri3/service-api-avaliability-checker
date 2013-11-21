package com.drfirst.mis.service_api_avaliability_checker;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dddtri.qa.api.cmd.Command;
import com.dddtri.qa.api.cmd.HttpServiceCallCommand;
import com.dddtri.qa.api.data.AbstractApiData;
import com.dddtri.qa.api.listener.Dao;
import com.dddtri.qa.api.listener.HttpResponseVerifierObserver;
public class MisSupportApiData extends AbstractApiData {

	
	public static final String URI = "/servlet/rcopia.servlet.EngineServlet";
	
	public static final String INPUT_XML_NAME = "xml";
	
	public static final String INPUT_TIME_NAME = "time";
	
	public static final String INPUT_AUTH_NAME = "skipAuth";
	
	public static final String INPUT_MAC_NAME = "MAC";
	
	public static final String OUTPUT_RESULT_MAP = "resultMap";
	
	public static final String OUTPUT_TESTCASE_NAME = "output.testcase.name";
	

	private Map<String, List<String>> paras;
	
	public MisSupportApiData(String baseUrl, Map<String, List<String>> paras) {
		super(baseUrl, paras);
		this.setHttpType(RequestType.GET);
		this.setSlug(URI);
		
		
		this.registerVerifierObserver(new HttpResponseVerifierObserver() {

			public Object assertBody(Object... args) {
				ResultMap resultMap = null;
	
				AbstractApiData apiData = (AbstractApiData) args[0];
				String response=apiData.getResponse();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = null;
				try {
					dBuilder = dbFactory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Document doc = null;
				try {
					doc = dBuilder.parse(response);
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				doc.getDocumentElement().normalize();
				
				NodeList nodes = doc.getElementsByTagName("Response");
				if (nodes.getLength()>1){
					throw new RuntimeException("there are more than 1 nodes in response!");
				}
				Element responseEle = (Element) nodes.item(0);
				
				if (responseEle.getElementsByTagName("Status").getLength()>1){
					throw new RuntimeException("there are more than 1 nodes in <response> <status>!");
				}
				Node statusNodes = responseEle.getElementsByTagName("Status").item(0);
				String actStatus=statusNodes.getNodeValue();
				resultMap.setStatus(actStatus);
				if(actStatus != "ok"){
					throw new RuntimeException("the status is not ok!");
				}
							
				apiData.putMetadata(OUTPUT_RESULT_MAP, resultMap);

				return apiData;
			}
			});
	}

	public Dao[] getDaos() {
		return new Dao[]{};
	}

	public Command getCmdChain() {
		return new HttpServiceCallCommand(this);
	}
	
	public void setInputXml(String... xmls){
		List<String> contentList = this.paras.get(INPUT_XML_NAME);
		for(String xml : xmls){
			contentList.add(xml);
		}
		this.paras.put(INPUT_XML_NAME, contentList);
	}
		
	public void setInputXml(Collection <? extends String>xmls){
		List<String> contentList = this.paras.get(INPUT_XML_NAME);
		contentList.addAll(xmls);
		this.paras.put(INPUT_XML_NAME, contentList);
	}

}
