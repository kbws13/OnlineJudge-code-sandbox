package xyz.kbws.ojcodesandbox.security;

import java.security.Permission;

/**
 * @author kbws
 * @date 2023/11/5
 * @description: 默认安全管理器（禁用所有权限）
 */
public class DenySecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission permission) {
        throw new SecurityException("权限异常:" + permission.toString());
    }
}
