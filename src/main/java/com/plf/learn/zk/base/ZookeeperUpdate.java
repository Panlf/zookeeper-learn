package com.plf.learn.zk.base;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperUpdate {

	String IP = "127.0.0.1:2181";
	ZooKeeper zookeeper;

	@Before
	public void before() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		zookeeper = new ZooKeeper(IP, 5000, new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.SyncConnected) {
					System.out.println("连接创建成功");
					latch.countDown();
				}
			}
		});

		// 主线程阻塞等待连接对象的创建成功
		latch.await();
	}

	@After
	public void after() throws Exception {
		zookeeper.close();
	}

	@Test
	public void set1() throws Exception {
		//数据版本号	-1代表版本号不参与更新
		Stat stat = zookeeper.setData("/set/test1", "test1".getBytes(), 1);
		System.out.println(stat.getVersion());
	}
	
	@Test
	public void set2() throws Exception {
		zookeeper.setData("/set/test2", "test2".getBytes(), 1,new AsyncCallback.StatCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				//rc 0 代表更新成功
				//path 节点的路径
				//ctx 上下文参数
				//stat 属性描述对象
			}
		},"I am context");
		Thread.sleep(5000);
		System.out.println("结束");
	}
}
