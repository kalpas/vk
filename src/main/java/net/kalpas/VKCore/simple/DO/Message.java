package net.kalpas.VKCore.simple.DO;


import org.joda.time.DateTime;

import net.kalpas.VKCore.simple.DO.WallPost.Attachment;

public class Message {

	public Long			id;
	public String		date;
	public int			out;		  // 0 incoming, 1 otherwise
	public String		user_id;
	public int			read_state;
	public String		title;
	public String		body;
	public String		chat_id;
	public String[]		chat_active;
	public PushSettings	push_settings;
	public String		admin_id;
	public Attachment[]	attachments;
	public Message[]	fwd_messages;

	public class PushSettings {
		public int	sound;
		public long	disabled_until;
	}

	public DateTime getDate() {
		return new DateTime(Long.valueOf(date) * 1000L);
	}
}
