package kalpas.VKCore.simple;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

public class ESPlayground {

	@Test
	public void main() throws ElasticsearchException, IOException {
		Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

		IndexResponse response = client
		        .prepareIndex("vk", "post", "1")
		        .setSource(
		                jsonBuilder().startObject().field("user", "kalpas").field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch").endObject()).execute().actionGet();

		// on shutdown
		client.close();
	}

}
