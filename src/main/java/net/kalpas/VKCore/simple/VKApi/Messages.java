package net.kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.kalpas.VKCore.simple.DO.Dialog;
import net.kalpas.VKCore.simple.DO.Message;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.client.Result;
import net.kalpas.VKCore.simple.VKApi.client.VKClient;

@Component
public class Messages {// TODO WIP

	private static final String	 GET_DIALOGS = "messages.getDialogs";
	private static final String	 GET		 = "messages.get";
	private static final Integer MAX_DIALOGS = 200;

	protected Logger			 logger		 = LogManager.getLogger(getClass());

	@Autowired
	private Gson				 gson;
	@Autowired
	private JsonParser			 parser;
	@Autowired
	private MapJoiner			 joiner;

	private VKClient			 client;

	@Autowired
	public Messages(VKClient client) {
		this.client = client;
	}

	public List<Message> get(long count, int out) {
		ArrayList<Message> messages = new ArrayList<>();

		// ResponseWrapper first = getChunk(0);
		// Long total = (long) Math.ceil(first.count/2.0);

		long l = (count / 200) + 1;
		for (long i = 0; i < l; i++) {

			ResponseWrapper chunk = getChunk(i * 200, out);
			if (chunk != null && chunk.response != null && chunk.response.items != null) {
				Collections.addAll(messages, chunk.response.items);
			}
		}

		return messages;
	}

	public List<Message> getAllIn() {
		return getAll(0);
	}

	public List<Message> getAllOut() {
		return getAll(1);
	}

	private List<Message> getAll(int out) {
		ArrayList<Message> messages = new ArrayList<>();
		long l = (getCount(out) / 200) + 1;
		for (long i = 0; i < l; i++) {

			ResponseWrapper chunk = getChunk(i * 200, out);
			if (chunk != null && chunk.response != null && chunk.response.items != null) {
				Collections.addAll(messages, chunk.response.items);
			}
		}

		return messages;
	}

	private ResponseWrapper getChunk(long offset, int out) {
		ArrayList<Message> chunk = new ArrayList<>();
		Result result = client.send(request(offset, 0, out));
		if (result.errCode != null) {
			return null;
		}

		String json;
		ResponseWrapper responseWrapper = null;
		try {
			json = IOUtils.toString(result.stream, "UTF-8");
			logger.error(json);
			responseWrapper = gson.fromJson(json, ResponseWrapper.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseWrapper;

	}

	private Long getCountIn() {
		return getCount(0);
	}

	private Long getCountOut() {
		return getCount(1);
	}

	private Long getCount(int out) {
		Result result = client.send(request(0, 0, out));
		if (result.errCode != null) {
			return 0l;
		}

		ResponseWrapper responseWrapper = null;
		String json;
		try {
			json = IOUtils.toString(result.stream, "UTF-8");
			logger.error(json);
			responseWrapper = gson.fromJson(json, ResponseWrapper.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseWrapper.response.count;
	}

	private String request(long offset, int out) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("count", "200");
		params.put("offset", String.valueOf(offset));
		params.put("out", String.valueOf(out));
		return GET + "?" + joiner.join(params);
	}

	private String request(long offset, long count, int out) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("count", String.valueOf(count));
		params.put("offset", String.valueOf(offset));
		params.put("out", String.valueOf(out));
		return GET + "?" + joiner.join(params);
	}

	public List<Dialog> getDialogs(Integer count) throws VKError {
		return getDialogs(count, 0);
	}

	public List<Dialog> getDialogs(Integer count, Integer offset) throws VKError {
		List<Dialog> dialogs = new ArrayList<>();
		Result result = client.send(buildRequest(count, offset));
		if (result.errCode != null) {
			VKError error = new VKError(result.errMsg);
			throw error;
		}
		try {
			String json = IOUtils.toString(result.stream, "UTF-8");
			JsonObject object = parser.parse(json).getAsJsonObject();
			JsonArray array = object.getAsJsonArray("response");
			if (array == null) {
				throw VKError.fromJSON(json);
			}
			Iterator<JsonElement> iterator = array.iterator();
			if (iterator.hasNext())
				iterator.next();// just skipping element containing count of all
				                // dialogs
			while (iterator.hasNext()) {
				dialogs.add(gson.fromJson(iterator.next(), Dialog.class));
			}
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			logger.error(e);
			VKError error = new VKError(e.toString());
			throw error;
		}
		return dialogs;
	}

	public List<Dialog> getDialogs(int days) throws VKError {
		DateTime now = new DateTime();
		List<Dialog> dialogs = new ArrayList<>();
		Dialog last;
		do {
			dialogs.addAll(getDialogs(MAX_DIALOGS, 0));
			last = Iterables.getLast(dialogs);
		} while (postIsNotThatOld(days, now, last));
		Iterator<Dialog> iterator = dialogs.iterator();
		while (iterator.hasNext()) {
			if (!postIsNotThatOld(days, now, iterator.next()))
				iterator.remove();// FIXME finished here
		}
		return dialogs;
	}

	private boolean postIsNotThatOld(int days, DateTime now, Dialog last) {
		return new DateTime(Long.valueOf(last.date) * 1000L).isAfter(now.minusDays(days));
	}

	private String buildRequest(Integer count, Integer offset) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("count", count.toString());
		params.put("offset", offset.toString());
		return GET_DIALOGS + "?" + joiner.join(params);

	}

	public class ResponseWrapper {
		public Response response;
	}

	public class Response {
		public Long	   count;
		public Message items[];
	}
}
