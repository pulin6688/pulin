package com.pulin.test;

/*
 * 创建线程造成oom
 * 这个还是不要测试了。如果你的机器够强筋，算我没说~~~
 * 话说我重启了~~
 * 
 */
public class CreateTheadOOM {

	private static void loop() {
		while (true) {
		}
	}

	private void runThead() {
		while (true) {
			new Thread(new Runnable() {
				public void run() {
					loop();
				}
			}).start();

		}
	}

	public static void main(String[] args) {
		new CreateTheadOOM().runThead();
	}
}
