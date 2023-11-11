package xyz.kbws.ojcodesandbox.security;

import java.security.Permission;

/**
 * @author kbws
 * @date 2023/11/5
 * @description: 默认安全管理器（所有权限放开）
 */
public class DefaultSecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission permission) {
        System.out.println("默认不做任何限制");
        System.out.println(permission);
//        super.checkPermission(permission);
    }
}
