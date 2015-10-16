package net.kalpas.VKCore.stats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;

import net.kalpas.VKCore.simple.DO.Message;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Messages;
import net.kalpas.VKCore.simple.VKApi.Users;
import net.kalpas.VKCore.simple.helper.CSVHelper;

@Component
public class MessageInsights {

	@Autowired
	private Messages messages;

	@Autowired
	private Users	 users;

	public void niceTry() throws IOException, VKError {
		List<Message> in = getCached(115826);
		// List<Message> out = messages.get(1000,1);
		TreeMultiset<User> total = TreeMultiset.create();
		Multiset<String> wordCount = HashMultiset.create();
		List<Day> matrix = new ArrayList<>();

		// .withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
		DateTime today = new DateTime().withMillisOfDay(0);
		Day current = new Day(today);

		for (Message message : in) {
			User sender = getCached(message.user_id);

			wordCount.addAll(splitter.splitToList(message.body));
			total.add(sender);

			if (current.day.isAfter(message.getDate())) {
				matrix.add(current);
				current = new Day(current.day.minusDays(1));
			}
			current.data.add(message.user_id);

		}

		double average = total.entrySet().stream().map(Entry::getCount)
		        .collect(Collectors.averagingInt(value -> value));

		// ImmutableMultiset<User> filtered =
		// Multisets.copyHighestCountFirst(total).entrySet().stream().filter(entry
		// -> );

		CSVHelper helper = new CSVHelper("all");

		List<String> header = new ArrayList<>();
		header.add("Date");
		header.addAll(total.elementSet().stream().map(user -> String.format("%s %s", user.first_name, user.last_name))
		        .collect(Collectors.toList()));
		helper.writeHeader(header);

		for (Day day : matrix) {
			List<String> row = new ArrayList<>();
			row.add(day.toString());
			row.addAll(total.elementSet().stream().map(user -> String.valueOf(day.data.count(user.id)))
			        .collect(Collectors.toList()));
			helper.writeRow(row);
		}

		helper.close();

		Multisets.copyHighestCountFirst(wordCount).entrySet().forEach(entry -> {
			System.out.println(String.format("%s %d", entry.getElement(), entry.getCount()));
		});
	}

	private User getCached(String id) {
		User cachedResult = null;
		try {
			cachedResult = userList.get(id);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return cachedResult;
	}

	public List<Message> getCached(int count) {
		List<Message> msgs = null;
		try {
			msgs = messageCache.get(count);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return msgs;

	}

	private LoadingCache<Integer, List<Message>> messageCache = CacheBuilder.newBuilder()
	        .build(new CacheLoader<Integer, List<Message>>() {
		        public List<Message> load(Integer count) throws VKError {
			        return messages.get(count, 0);
		        }
	        });

	private LoadingCache<String, User>			 userList	  = CacheBuilder.newBuilder()
	        .build(new CacheLoader<String, User>() {
		        public User load(String id) throws VKError {
			        return users.get(id);
		        }
	        });

	private Splitter							 splitter	  = Splitter.on(CharMatcher.anyOf(" ,.!?:;'\"()-_=+*/"))
	        .omitEmptyStrings().trimResults();

	private Splitter							 splitter2	  = Splitter.on(CharMatcher.JAVA_ISO_CONTROL).trimResults()
	        .omitEmptyStrings();

	class Day {
		public final DateTime		day;
		public TreeMultiset<String>	data = TreeMultiset.create();

		Day(DateTime day) {
			this.day = day;
		}

		@Override
		public String toString() {
			return day.toString();
		}
	}

}
