package test.util.http;

import org.apache.http.client.methods.HttpPost;

public class HttpCustomDelete extends HttpPost{

	public HttpCustomDelete(String uri){
		super(uri);
	}
	
	@Override
	public String getMethod(){
		return "DELETE";
	}
	
}
