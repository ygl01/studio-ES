package com.kuang;

import com.alibaba.fastjson.JSON;
import com.kuang.pojo.User;
import com.sun.org.apache.bcel.internal.ExceptionConst;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
class KuangshenEsApiApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	/**
	 * 创建索引
	 * @throws IOException
	 */
	@Test
    void testCreateIndex() throws IOException {
    	//创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("kuang_index");
		//客户端执行请求 IndicesClient,请求后获取响应
		CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}

	/**
	 * 测试获取索引
	 */
	@Test
	void testExistIndex() throws IOException {

		GetIndexRequest request = new GetIndexRequest("kuang_index");
		boolean response = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}

	/**
	 * 删除索引
	 * @throws IOException
	 */
	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
		AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(delete);
	}

	@Test
	void testAddDocument() throws IOException{
		//创建对象
		User user = new User("狂神说update",3);
		//创建请求
		IndexRequest request = new IndexRequest("kuang_index");
		//规则   put/kuang_index/_doc/1
		request.id("1");
		request.timeout("1s");

		//将我们的数据放入请求   json
		request.source(JSON.toJSONString(user), XContentType.JSON);

		//客户端发送请求 获取相应结果

		IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);

		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());
	}

	/**
	 * 获取文档
	 */
	@Test
	void getDocument() throws Exception {
		GetRequest request = new GetRequest("kuang_index", "1");
		boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//获取文档信息
	@Test
	void testGetDocument() throws Exception{
		GetRequest request = new GetRequest("kuang_index", "1");
		//判断是否存在
//		boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
		//取到该对象
		GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
//		documentFields.getSourceAsString();
		System.out.println(documentFields.getSourceAsString());
	}

	//更新文档信息
	@Test
	void testUpdateDocument() throws Exception{
		UpdateRequest request = new UpdateRequest("kuang_index", "1");
		request.timeout("1s");
		User user = new User("狂神说Java", 23);
		request.doc(JSON.toJSONString(user),XContentType.JSON);
		UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);

//		documentFields.getSourceAsString();
		System.out.println(update);
	}

	//删除文档信息
	@Test
	void testDeleteDocument() throws IOException {
		DeleteRequest request = new DeleteRequest("kuang_index", "1");
		DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}

	//批处理请求
	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("kaungshen1",23));
		userList.add(new User("kaungshen2",2));
		userList.add(new User("kaungshen3",26));
		userList.add(new User("kaungshen4",24));
		for (int i = 0; i < userList.size(); i++) {
			bulkRequest.add(
					new IndexRequest("kuang_index")
					.id(""+(i+1))
					.source(JSON.toJSONString(userList.get(i)),XContentType.JSON)
			);
		}
		restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
	}

	//查询
	@Test
	void testSearch() throws IOException {
		SearchRequest request = new SearchRequest("kuang_index");
		//构建搜索条件
		SearchSourceBuilder builder = new SearchSourceBuilder();

		//查询条件   可以使用QueryBuilder
		TermQueryBuilder query = QueryBuilders.termQuery("name", "kaungshen1");
		builder.query(query);
		request.source(builder);
		SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
		String s = JSON.toJSONString(search.getHits());
		System.out.println(s);
		System.out.println("============");
		for (SearchHit documentFields : search.getHits().getHits()){

			Map<String, Object> map = documentFields.getSourceAsMap();
			System.out.println(map);
		}

//		restHighLevelClient.search(builder)
	}


}
