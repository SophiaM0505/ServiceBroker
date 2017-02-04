package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * AHE file XML entity
 * @author davidc
 *
 */

@Root
public class file {

	@Element
	private String filename;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}