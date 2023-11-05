package xyz.kbws.ojcodesandbox.unsafe;

/**
 * @author kbws
 * @date 2023/11/3
 * @description: 无限睡眠
 */
public class SleepError {
    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 60 * 60 * 1000L;
        Thread.sleep(ONE_HOUR);
        System.out.println("睡完了");
    }
}
