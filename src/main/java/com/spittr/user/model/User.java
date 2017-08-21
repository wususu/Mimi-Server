package com.spittr.user.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="user")
@JsonIgnoreProperties({"plogin", "id"})
public class User implements Serializable{

	private static final long serialVersionUID = 7302289073823113966L;
	
	public User() {
	}
	
	public User(String uname){
		this(uname, null);
	}
	
	public User (String uname, String nname) {
		this(uname, nname, new Date(), true);
	}
	
	public User(String uname, String nname, Date tmCreated, Boolean plogin) {
		// TODO Auto-generated constructor stub
		this.uname = uname;
		this.nname = nname;
		this.tmCreated = tmCreated;
		this.plogin = plogin;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="uid")
	private Long uid;
	
	@Column(name="uname", unique=true, nullable=false)
	private String uname;
	
	@Column(name="nname", unique=true)
	private String nname;
	
	@Column(name="login_permission", nullable=false)
	private Boolean plogin;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", nullable=true)
	private Date tmCreated;
	
	public Long getUid(){
		return uid;
	}
	
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getNname() {
		return nname;
	}

	public void setNname(String nname) {
		this.nname = nname;
	}

	public Date getTmCreated() {
		return tmCreated;
	}

	public void setTmCreated(Date tmCreated) {
		this.tmCreated = tmCreated;
	}

	public Boolean getPlogin() {
		return plogin;
	}

	public void setPlogin(Boolean plogin) {
		this.plogin = plogin;
	}

	@Override
	public String toString() {
		return "User [id=" + uid + ", uname=" + uname + ", nname=" + nname + ", plogin=" + plogin + ", tmCreated="
				+ tmCreated + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		result = prime * result + ((nname == null) ? 0 : nname.hashCode());
		result = prime * result + ((plogin == null) ? 0 : plogin.hashCode());
		result = prime * result + ((tmCreated == null) ? 0 : tmCreated.hashCode());
		result = prime * result + ((uname == null) ? 0 : uname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		if (nname == null) {
			if (other.nname != null)
				return false;
		} else if (!nname.equals(other.nname))
			return false;
		if (plogin == null) {
			if (other.plogin != null)
				return false;
		} else if (!plogin.equals(other.plogin))
			return false;
		if (tmCreated == null) {
			if (other.tmCreated != null)
				return false;
		} else if (!tmCreated.equals(other.tmCreated))
			return false;
		if (uname == null) {
			if (other.uname != null)
				return false;
		} else if (!uname.equals(other.uname))
			return false;
		return true;
	}
	
	
	
}