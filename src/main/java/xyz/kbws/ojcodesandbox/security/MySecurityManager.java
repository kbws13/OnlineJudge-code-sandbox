package xyz.kbws.ojcodesandbox.security;

import java.security.Permission;

/**
 * @author kbws
 * @date 2023/11/5
 * @description: 默认安全管理器
 */
public class MySecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission perm) {
        super.checkPermission(perm);
    }

    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("权限异常: " + cmd);
    }

    @Override
    public void checkRead(String file) {
        throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkWrite(String file) {
        throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkDelete(String file) {
        throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("权限异常: " + host + ":" + port);
    }
}
