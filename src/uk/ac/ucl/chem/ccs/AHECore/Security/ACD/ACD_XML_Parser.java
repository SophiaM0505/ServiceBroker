package uk.ac.ucl.chem.ccs.AHECore.Security.ACD;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.ucl.chem.ccs.AHECore.Security.ACD.objects.ACDProxy;

/**
 * Handles ACD XML messages
 * @author davidc
 *
 */

public class ACD_XML_Parser {

	DocumentBuilderFactory dbf;
	DocumentBuilder db;
	private Document doc;
	
	
	public static void main(String[] arg){
		
		ACD_XML_Parser test = new ACD_XML_Parser();
		try {
			boolean x = ACD_XML_Parser.authenticateUser("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><acd><timestamp>4 Jan 2012 12:39:17 GMT</timestamp><command>login</command><User id=\"1016\"/></acd>");
			boolean y = ACD_XML_Parser.authenticateUser("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><acd><timestamp>4 Jan 2012 12:39:17 GMT</timestamp><command>login</command><exception>acd.exceptions.AuthenticationOPException: Invalid_Username_Password</exception></acd>");
			System.out.println(y);
			
			ACDProxy p = ACD_XML_Parser.generateUserProxy("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><acd><timestamp>4 Jan 2012 12:50:34 GMT</timestamp><command>generateProxies</command><exception>0</exception></acd>");
			
			System.out.println(p);
			
			p = ACD_XML_Parser.generateUserProxy("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><acd><timestamp>4 Jan 2012 12:48:14 GMT</timestamp><command>generateProxies</command>    <proxy> <username>acduser254926954</username> <password>acduser62586123</password><subject_dn>/C=UK/O=eScience/OU=UCL/L=EISD/CN=bunsen.chem.ucl.ac.uk/E=stefan.zasada@ucl.ac.uk</subject_dn> <uri>bunsen.chem.ucl.ac.uk</uri> <lifetime>84400</lifetime> <port>7512</port> </proxy></acd>");
			
			System.out.println(p.getUsername());
			System.out.println(p.getPassword());
			System.out.println(p.getPort());
			System.out.println(p.getSubject_dn());
			System.out.println(p.getLifetime());
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static boolean authenticateUser(String xml) throws SAXException, IOException, ParserConfigurationException{
	
		
		ACD_XML_Parser p = new ACD_XML_Parser();
		p.parse(xml);
		
		NodeList l = p.getDoc().getElementsByTagName("User");
		
		if(l.getLength() > 0){
			return true;
		}
		
		return false;
	}
	
	public static ACDProxy generateUserProxy(String xml) throws SAXException, IOException, ParserConfigurationException{
		
		ACD_XML_Parser p = new ACD_XML_Parser();
		p.parse(xml);
		
		NodeList l = p.getDoc().getElementsByTagName("proxy");
		
		if(l.getLength() == 0)
			return null;
		
		ACDProxy proxy = new ACDProxy();
	
		Element proxy_element = (Element) l.item(0);
		
		NodeList ulist = proxy_element.getElementsByTagName("username");
		
		if(ulist.getLength() > 0){
			
			Element elem = (Element) ulist.item(0);
			proxy.setUsername(elem.getTextContent());
			
		}else{
			return null;
		}
		
		NodeList plist = proxy_element.getElementsByTagName("password");
		
		if(plist.getLength() > 0){
			
			Element elem = (Element) plist.item(0);
			proxy.setPassword(elem.getTextContent());
			
		}else{
			return null;
		}
		
		NodeList sbj_list = proxy_element.getElementsByTagName("subject_dn");
		
		if(sbj_list.getLength() > 0){
			
			Element elem = (Element) sbj_list.item(0);
			proxy.setSubject_dn(elem.getTextContent());
			
		}else{
			return null;
		}
		
		NodeList uri_llist = proxy_element.getElementsByTagName("uri");
		
		if(uri_llist.getLength() > 0){
			
			Element elem = (Element) uri_llist.item(0);
			proxy.setUri(elem.getTextContent());
			
		}else{
			return null;
		}
		
		NodeList port_list = proxy_element.getElementsByTagName("port");
		
		if(port_list.getLength() > 0){
			
			Element elem = (Element) port_list.item(0);
			proxy.setPort(elem.getTextContent());
			
		}else{
			return null;
		}
		
		NodeList life_list = proxy_element.getElementsByTagName("lifetime");
		
		if(life_list.getLength() > 0){
			
			Element elem = (Element) life_list.item(0);
			proxy.setLifetime(elem.getTextContent());
			
		}else{
			return null;
		}
		
		return proxy;
	}
	

	
	private Document parse(String document) throws SAXException, IOException, ParserConfigurationException{
		return parse(new StringReader(document));
	}
	
	private Document parse(Reader document) throws SAXException, IOException, ParserConfigurationException {    

		InputSource source = new InputSource(document);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		setDoc(db.parse(source));

//		if(schema){
//
//			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//			File schemaLocation = new File(schema_file);
//			Schema schema = factory.newSchema(schemaLocation);
//			Validator validator = schema.newValidator();
//
//
//			DOMSource domsource = new DOMSource(doc);
//
//			try {
//				validator.validate(domsource);
//			}catch (SAXException ex) {
//				ex.printStackTrace();
//			}  

//		}


		return getDoc();

	}
	
	private void createDocument(){
		
		try{

			this.dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			this.db = dbf.newDocumentBuilder();
			//db.setErrorHandler(new ValidationErrorHandler());
			setDoc(db.newDocument());
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	public String toString(){

		if(getDoc() == null)
			return "NULL";

		try{	

			TransformerFactory tFactory =
				TransformerFactory.newInstance();
//			tFactory.setAttribute("indent-number", 4);
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter writer = new StringWriter(256);
			DOMSource source = new DOMSource(getDoc());
			transformer.transform(source,new StreamResult(writer));

			return writer.toString();

		}catch (Exception e){
			e.printStackTrace();
		}

		return "";

	}
	
}

