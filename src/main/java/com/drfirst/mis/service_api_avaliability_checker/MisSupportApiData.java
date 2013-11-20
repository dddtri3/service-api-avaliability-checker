package com.drfirst.mis.service_api_avaliability_checker;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dddtri.qa.api.cmd.Command;
import com.dddtri.qa.api.cmd.HttpServiceCallCommand;
import com.dddtri.qa.api.data.AbstractApiData;
import com.dddtri.qa.api.listener.Dao;
import com.dddtri.qa.api.listener.AbstractDbVerifierObserver;
import com.dddtri.qa.api.listener.HttpResponseVerifierObserver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MisSupportApiData extends AbstractApiData {

	public static final String URI = "/servlet/rcopia.servlet.EngineServlet";
	
	public static final String INPUT_XML_NAME = "xml";

	private Map<String, List<String>> paras;
	
	public MisSupportApiData(String baseUrl, Map<String, List<String>> paras) {
		super(baseUrl, paras);
		this.setHttpType(RequestType.GET);
		this.setSlug(URI);
		
		
		this.registerVerifierObserver(new HttpResponseVerifierObserver() {

			public Object assertBody(Object... args) {
				AbstractApiData apiData = (AbstractApiData) args[0];
				
				//extract instance name
				//ex: Server uptime in milisecond -- 1004255746; Server/Instance Name: ctintweb01_CERT_moxyadmin Server Version: MoxyAdmin 2.0.2
				//ex: Server/Instance Name: My Server Name; Server Version: 1.0.0
				final String pattern = ".+Instance\\sName:\\s+(.*)\\s+Server\\sVersion:\\s+?(.*)";
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(apiData.getResponse());
				m.find();
				String instName = m.group(1);
				apiData.putMetadata(METADATA_ID_INSTANCENAME, instName);
				return apiData;
			}
			
		});
	}

	@Override
	public Dao[] getDaos() {
		return new Dao[]{};
	}

	@Override
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
