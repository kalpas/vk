package net.kalpas.VKCore.simple.DO;

import java.io.Serializable;

import com.google.common.base.Joiner;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionid = -4504087520562844466L;

	public final String		  id;
	public String			  first_name;
	public String			  last_name;
	public String			  nickname;
	// 2 - male, 1 - female
	public String			  sex;
	public String			  bdate;
	public City				  city;
	public Country			  country;
	public String			  photo_50;
	public String			  photo_100;
	public String			  photo_200_orig;
	public String			  photo_200;
	public String			  photo_max;
	public String			  photo_max_orig;
	public String			  online;
	public String[]			  lists;
	public String			  screen_name;
	public String			  has_mobile;
	public String			  rate;
	public Contacts			  contacts;
	public String			  education;
	public String			  universities;
	public String			  schools;
	public String			  can_post;
	public String			  can_see_all_posts;
	public String			  can_write_public_message;
	public String			  activity;
	public LastSeen			  last_seen;
	// 1 - не женат/не замужем
	// 2 - есть друг/есть подруга
	// 3 - помолвлен/помолвлена
	// 4 - женат/замужем
	// 5 - всё сложно
	// 6 - в активном поиске
	// 7 - влюблён/влюблена
	public String			  relation;
	public Counters			  counters;
	public Exports			  exports;
	public String			  wall_comments;
	public Relatives[]		  relatives;
	public String			  interests;
	public String			  movies;
	public String			  tv;
	public String			  books;
	public String			  games;
	public String			  about;
	public String[]			  connections;
	public String			  timezone;
	public String			  photo;
	public String			  photo_medium;
	public String			  photo_big;
	public String			  domain;

	public User(String id) {
		this.id = id;
	}

	public class Exports {
		public String facebook;
	}

	public class City {
		public String id;
		public String title;
	}

	public class Country {
		public String id;
		public String title;
	}

	@Override
	public String toString() {
		return Joiner.on(" ").skipNulls().join(id, first_name, last_name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;

	}
}
