package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "member_generator")
	@SequenceGenerator(name = "member_generator", sequenceName = "member_id_seq", allocationSize = 1)
	Integer id;


	String name;

	@ManyToOne
	@JoinColumn(name = "block_id", referencedColumnName = "id")
	Block block;

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

	public Block getBlock() {
		return block;
	}

	public void setBlock(final Block block) {
		this.block = block;
	}

}
