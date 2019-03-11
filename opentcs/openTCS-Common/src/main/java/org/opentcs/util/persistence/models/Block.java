package org.opentcs.util.persistence.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Block {

	@Id
	@Column(name = "id", unique = true, nullable = false, insertable=false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "block_generator")
	@SequenceGenerator(name = "block_generator", sequenceName = "block_id_seq", allocationSize = 1)
	Integer id;

	String name;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Member.class)
	List<Member> members;

	@ManyToOne
	@JoinColumn(name = "model_id", referencedColumnName = "id")
	Model model;

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(final List<Member> members) {
		this.members = members;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(final Model model) {
		this.model = model;
	}

}
