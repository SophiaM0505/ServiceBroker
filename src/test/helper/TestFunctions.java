package test.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import negotiation.HibernateImp.Contract;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;

public class TestFunctions {

	public static void main(String[] args) throws AHESecurityException {
		//AHEUser check_user = SecurityUserAPI.getUser("Sofia");
        //String group = check_user.getAcd_vo_group();
        //System.out.println("hello " + group);
		Contract contract = new Contract();
		contract.setId(7303);
		String provider = contract.getService().getProvider();
		System.out.println(provider);
	}
	
	public static void writeout(String content) {
		try {
			File file = new File("/Users/zeqianmeng/Documents/workspace/AHE3/test_data/test.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
