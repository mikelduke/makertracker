package org.tenbitworks.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="interlock_log")
public class InterlockLogEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String rfid;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="asset")
	private Asset asset;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member")
	private Member member;

	@NotNull
	private Date logDate;
	
	@NotNull
	private String event;

	public InterlockLogEntry() { 
		this.logDate = Calendar.getInstance().getTime();
	}

	public InterlockLogEntry(String rfid, Asset asset, Member member, String event) {
		this.rfid = rfid;
		this.asset = asset;
		this.member = member;
		this.event = event;
		this.logDate = Calendar.getInstance().getTime();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Date getLogDate() {
		return logDate;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return "InterlockLogEntry [id=" + id + ", rfid=" + rfid + ", asset=" + asset + ", member=" + member
				+ ", logDate=" + logDate + ", event=" + event + "]";
	}

}
