package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * AHE ahe XML entity.
 * This is the root class of all ahe XML messages. This entity encapsulates all the other ahe XML entities.
 * @author davidc
 *
 */

@Root
public class ahe {

	//Basic Messages Structure
	@Element(required=false)
	String command;
	@Element(required=false)
	String timestamp;
	
	@Element(required=false)
	String information;
	@Element(required=false)
	String warning;
	@Element(required=false)
	String exception;
	
	//App-ArrayList message
	@ElementList(required=false)
	ArrayList<app> app_list;
	
	@Element(required=false)
	private
	app app;
	
	//App-Instance ArrayList Message
	
	@ElementList(required=false)
	ArrayList<app_instance> appinst_list;
	
	//App-Instance Specific Message
	
	@Element(required=false)
	private
	app_instance app_instance;
	
	//Transfer ArrayList message
	
	@ElementList(required=false)
	ArrayList<transfer> transfer_list;
	
	//Transfer message
	
	@Element(required=false)
	transfer transfer;
	
	//Resource ArrayList message
	
	@ElementList(required=false)
	ArrayList<resource> resource_list;
	
	//Resource message
	
	@Element(required=false)
	resource res;
	
	//Credential ArrayList message
	
	@ElementList(required=false)
	ArrayList<credential> cred_list;
	
	//Credential Message
	
	@Element(required=false)
	credential cred;
	
	//User ArrayList message
	
	@ElementList(required=false)
	ArrayList<user> user_list;
	
	@ElementList(required=false)
	private
	ArrayList<file> file_list;
	
	//User Message
	@Element(required=false)
	user user;
	
	//Property List message
	@ElementList(required=false)
	private
	ArrayList<property> property_list;
	
	@Element(required=false)
	private
	property property;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public ArrayList<app> getApp_list() {
		return app_list;
	}

	public void setApp_list(ArrayList<app> app_list) {
		this.app_list = app_list;
	}

	public ArrayList<app_instance> getAppinst_list() {
		return appinst_list;
	}

	public void setAppinst_list(ArrayList<app_instance> appinst_list) {
		this.appinst_list = appinst_list;
	}

	public ArrayList<transfer> getTransfer_list() {
		return transfer_list;
	}

	public void setTransfer_list(ArrayList<transfer> transfer_list) {
		this.transfer_list = transfer_list;
	}

	public transfer getTransfer() {
		return transfer;
	}

	public void setTransfer(transfer transfer) {
		this.transfer = transfer;
	}

	public ArrayList<resource> getResource_list() {
		return resource_list;
	}

	public void setResource_list(ArrayList<resource> resource_list) {
		this.resource_list = resource_list;
	}

	public resource getRes() {
		return res;
	}

	public void setRes(resource res) {
		this.res = res;
	}

	public ArrayList<credential> getCred_list() {
		return cred_list;
	}

	public void setCred_list(ArrayList<credential> cred_list) {
		this.cred_list = cred_list;
	}

	public credential getCred() {
		return cred;
	}

	public void setCred(credential cred) {
		this.cred = cred;
	}

	public ArrayList<user> getUser_list() {
		return user_list;
	}

	public void setUser_list(ArrayList<user> user_list) {
		this.user_list = user_list;
	}

	public user getUser() {
		return user;
	}

	public void setUser(user user) {
		this.user = user;
	}

	public ArrayList<property> getProperty_list() {
		return property_list;
	}

	public void setProperty_list(ArrayList<property> property_list) {
		this.property_list = property_list;
	}

	public property getProperty() {
		return property;
	}

	public void setProperty(property property) {
		this.property = property;
	}

	public app getApp() {
		return app;
	}

	public void setApp(app app) {
		this.app = app;
	}

	public app_instance getApp_instance() {
		return app_instance;
	}

	public void setApp_instance(app_instance app_instance) {
		this.app_instance = app_instance;
	}

	public ArrayList<file> getFile_list() {
		return file_list;
	}

	public void setFile_list(ArrayList<file> file_list) {
		this.file_list = file_list;
	}
	
	
	
	
}

