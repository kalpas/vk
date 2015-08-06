package net.kalpas.VKCore.stats;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Friends;
import net.kalpas.VKCore.simple.VKApi.Users;

@Component
public class FriendStats {

	private Logger  logger = LogManager.getLogger(FriendStats.class);

	@Autowired
	private Friends friends;

	@Autowired
	private Users   users;

	public Multimap<User, User> getNetwork(String id, boolean withMe) {
		User me = null;
		try {
			me = users.get(id);
		} catch (VKError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<User> friendList = null;
		friendList = friends.get(id);
		try {
			friendList = users.get(friendList);
		} catch (VKError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Multimap<User, User> network = ArrayListMultimap.create();
		if (withMe) {
			for (User friend : friendList) {
				network.put(me, friend);
			}
		}

		List<User> list = null;
		for (User friend : friendList) {
			list = friends.get(friend);
			for (User user : list) {
				if (friendList.contains(user)) {
					user = list.get(list.indexOf(user));// TODO consider smth
					                                    // a bit more beautiful
					network.put(friend, user);
				} else if (withMe && user.equals(me)) {
					network.put(friend, me);
				}
			}
		}

		return network;
	}

	/*public Multimap<User, User> getNetwork(List<String> ids, boolean withMe) {
		for (String id : ids) {
			User me = null;
			try {
				me = users.get(id);
			} catch (VKError e) {
				e.printStackTrace();
			}
			List<User> friendList = null;
			friendList = friends.get(id);
			try {
				friendList = users.get(friendList);
			} catch (VKError e) {
				e.printStackTrace();
			}

			Multimap<User, User> network = ArrayListMultimap.create();
			if (withMe) {
				for (User friend : friendList) {
					network.put(me, friend);
				}
			}

			List<User> list = null;
			for (User friend : friendList) {
				list = friends.get(friend);
				for (User user : list) {
					if (friendList.contains(user)) {
						user = list.get(list.indexOf(user));// TODO consider
						                                    // smth
						                                    // a bit more
						                                    // beautiful
						network.put(friend, user);
					} else if (withMe && user.equals(me)) {
						network.put(friend, me);
					}
				}
			}
		}

		return network;
	}*/

}
