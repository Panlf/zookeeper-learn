package com.plf.learn.zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorDelete {

	String ip = "127.0.0.1:2181";
	CuratorFramework client;

	@Before
	public void before() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

		client = CuratorFrameworkFactory.builder()
				// IP地址端口号127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
				.connectString(ip)
				// 会话超时时间
				.sessionTimeoutMs(5000)
				// 重试机制
				.retryPolicy(retryPolicy)
				// 命名空间
				.namespace("create").build();

		client.start();
	}

	@After
	public void after() {
		client.close();
	}

	@Test
	public void delete1() throws Exception{
		//删除节点
		client.delete()
			.forPath("/curator1");
		System.out.println("结束");
	}
	
	@Test
	public void delete2() throws Exception{
		client.delete()
			//版本号
			.withVersion(0)
			.forPath("/curator2");
		System.out.println("结束");
	}
	
	@Test
	public void delete3() throws Exception{
		//删除包含子节点的节点
		client.delete()
			.deletingChildrenIfNeeded()
			//版本号
			.withVersion(-1)
			.forPath("/curator3");
		System.out.println("结束");
	}
	
	@Test
	public void delete4() throws Exception{
		//异步方式删除节点
		client.delete()
			.deletingChildrenIfNeeded()
			//版本号
			.withVersion(-1)
			.inBackground(new BackgroundCallback() {
				@Override
				public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
					//节点的路径
					System.out.println(event.getPath());
					
					//事件类型
					System.out.println(event.getType());
				}
			})
			.forPath("/curator4");
		System.out.println("结束");
	}
}
