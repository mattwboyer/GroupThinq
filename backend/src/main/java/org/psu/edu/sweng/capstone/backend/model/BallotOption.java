package org.psu.edu.sweng.capstone.backend.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;

@Entity
@Table(name = "BALLOT_OPTION")
public class BallotOption {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BALLOT_ID")
	private Ballot ballot;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	private User user;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

	@OneToMany(mappedBy = "ballotOption", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = BallotResult.class)
	private Set<BallotResult> results = new HashSet<>();
	
	protected BallotOption() {}
	
	public BallotOption(String title, Ballot ballot, User user) {
		this.title = title;
		this.ballot = ballot;
		this.user = user;
		
		this.createdDate = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ballot getBallot() {
		return ballot;
	}

	public void setBallot(Ballot ballotId) {
		this.ballot = ballotId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Set<BallotResult> getResults() {
		return results;
	}

	public void setResults(Set<BallotResult> results) {
		this.results = results;
	}
}
