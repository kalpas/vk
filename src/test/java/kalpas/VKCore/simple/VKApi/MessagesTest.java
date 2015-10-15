package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.Dialog;
import net.kalpas.VKCore.simple.DO.Message;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Messages;
import net.kalpas.VKCore.simple.VKApi.Users;
import net.kalpas.VKCore.simple.helper.CSVHelper;

public class MessagesTest extends BaseApiTest {

	@Autowired
	private Messages messages;

	@Autowired
	private Users	 users;

	@Ignore
	@Test
	public void test() throws VKError {
		// do
		List<Dialog> result = messages.getDialogs(10);

		// assert
		assertFalse(result.isEmpty());
	}

	@Ignore
	@Test
	public void testWithOffset() throws VKError {
		// do
		List<Dialog> result = messages.getDialogs(10, 10);

		// verify
		assertFalse(result.isEmpty());
	}

	@Test
	public void get() {
		List<Message> msgs = messages.get(201, 0);
	}

	@Test
	public void getAll() throws IOException, VKError {
		List<Message> in = messages.get(1000, 0);
		// List<Message> out = messages.get(1000,1);

		Map<String, Map<String, Long>> matrix = new HashMap<>();

		Map<String, Long> user = null;
		for (Message message : in) {
			user = matrix.get(message.user_id);
			if (user == null) {
				user = new HashMap<>();
				matrix.put(message.user_id, user);
			}
			String date = message.getDate().toString();
			Long count = user.get(date);
			if (count == null) {
				count = 0L;
				user.put(date, count);
			}
			count++;
		}

		CSVHelper helper = new CSVHelper("test");

		for (Entry<String, Map<String, Long>> entry : matrix.entrySet()) {
			List<String> firstRow = new ArrayList<>();
			List<String> secondRow = new ArrayList<>();

			User person = users.get(entry.getKey());

			firstRow.add("name/date");
			secondRow.add(person.first_name + " " + person.last_name);
			for (Entry<String, Long> item : entry.getValue().entrySet()) {
				firstRow.add(item.getKey());
				secondRow.add(item.getValue().toString());
			}
			helper.writeRow(firstRow);
			helper.writeRow(secondRow);
		}

		helper.close();
		// helper.
	}

	@Test
	public void niceTry() throws IOException, VKError {
		List<Message> in = messages.get(115826, 0);
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

	private LoadingCache<String, User> userList	 = CacheBuilder.newBuilder().build(new CacheLoader<String, User>() {
		                                             public User load(String id) throws VKError {
			                                             return users.get(id);
		                                             }
	                                             });

	private Splitter				   splitter	 = Splitter.on(CharMatcher.anyOf(" ,.!?:;'\"()-_=+*/"))
	        .omitEmptyStrings().trimResults();

	private Splitter				   splitter2 = Splitter.on(CharMatcher.JAVA_ISO_CONTROL).trimResults()
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
