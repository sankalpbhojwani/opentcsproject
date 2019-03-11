package org.opentcs.util.persistence.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table (name = "xml_model")
public class XmlModel {

	@Id
	Integer id;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
  @Column (name = "xml_data")
	String xmlData;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getXmlData() {
		return xmlData;
	}

	public void setXmlData(final String xmlData) {
		this.xmlData = xmlData;
	}
}
