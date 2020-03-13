package com.plf.learn.zk.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorTransaction {

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
	public void transaction() throws Exception {
		CuratorOp createOp = client.transactionOp().create().forPath("/transaction", "transaction".getBytes());
		CuratorOp setDataOp = client.transactionOp().setData().withVersion(0).forPath("/transaction",
				"NEW DATA".getBytes());
		CuratorOp checkOp = client.transactionOp().check().withVersion(1).forPath("/transaction");
		CuratorOp deleteOp = client.transactionOp().delete().forPath("/transaction");

		List<CuratorTransactionResult> curatorTransactionResults = client.transaction().forOperations(createOp,
				setDataOp, checkOp, deleteOp);
		for (CuratorTransactionResult result : curatorTransactionResults) {
			System.out.println(result.getForPath() + "  " + result.getType() + "  " + result.getError());
		}
	}
}
