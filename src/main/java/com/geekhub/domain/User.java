package com.geekhub.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String email;
	private String name;
	private String password;

	@Column(unique = true)
	private String username;

	@ManyToOne
	private Authority authority;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy="user", orphanRemoval = true)
	@MapKeyColumn(name="id")
	private Map<Long, Comment> comments = new HashMap<>();
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy="manager", orphanRemoval = true)
	private Set<Hotel> hotels = new HashSet<>();

	public User() {
	}

	public User(String email, String name, String password, String username) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.username = username;
	}

	public Iterable<Comment> getComments() {
		return comments.values();
	}

	public String getEmail() {
		return email;
	}
	
	public Set<Hotel> getHotels() {
		return hotels;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setComments(Map<Long, Comment> comments) {
		this.comments = comments;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHotels(Set<Hotel> hotels) {
		this.hotels = hotels;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}