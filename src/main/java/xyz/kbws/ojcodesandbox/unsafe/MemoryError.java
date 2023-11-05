package xyz.kbws.ojcodesandbox.unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kbws
 * @date 2023/11/3
 * @description: 无限占用空间
 */
public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[10000]);
        }
    }
}
