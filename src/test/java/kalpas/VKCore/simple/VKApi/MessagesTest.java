package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
		List<Message> in = messages.get(1000,0);
		// List<Message> out = messages.get(1000,1);
		
		Map<String,Map<String, Long>> matrix = new HashMap<>();
		
		Map<String, Long> user = null;
		for(Message message : in){
			user = matrix.get(message.user_id);
			if(user ==null){
				user = new HashMap<>();
				matrix.put(message.user_id, user);
			}
			String date = message.getDate().toString();
			Long count = user.get(date);
			if(count == null){
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
//		helper.
	}

}
