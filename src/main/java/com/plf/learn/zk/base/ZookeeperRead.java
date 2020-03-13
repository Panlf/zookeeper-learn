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

public class ZookeeperRead {

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
	public void read1() throws Exception {
		Stat stat = new Stat();
		byte[] bytes = zookeeper.getData("/get/test1",false,stat);
		
		System.out.println(new String(bytes));
		
		System.out.println(stat.getVersion());
	}
	
	
	@Test
	public void read2() throws Exception {
		zookeeper.getData("/get/test2", false,new AsyncCallback.DataCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx,byte[] data,Stat stat) {
				//rc 0 代表读取成功
				//path 节点的路径
				//ctx 上下文参数
				//数据
				System.out.println(new String(data));
				//属性对象
				System.out.println(stat.getVersion());
			}
		},"I am context");
		Thread.sleep(5000);
		System.out.println("结束");
	}
}
