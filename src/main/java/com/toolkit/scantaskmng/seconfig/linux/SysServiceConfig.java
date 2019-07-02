package com.toolkit.scantaskmng.seconfig.linux;

import org.springframework.stereotype.Component;

@Component
public class SysServiceConfig {

    // https://help.aliyun.com/knowledge_detail/41212.html
    // 1. 修改 ssh 的连接端口，建议改成非标准的高端端口【1024到65535】
    // Port  22022
    // 2. 禁止 root 用户直接登录 ssh，用普通账号 ssh 连接，然后在切换到 root 账号登录。
    // 在/etc/ssh/sshd_config配置文件中设置以下参数
    // PermitRootLogin  no
    // 3. 限制 ssh 连接的 IP 地址，只允许用户指定的 IP 地址可以 ssh 连接服务器。
    // 修改 /etc/hosts.allow 和 /etc/hosts.deny 这两个配置文件。
    // vim /etc/hosts.deny     #设置禁止所有ip连接服务器的ssh。
    // sshd:all:deny
    // vim  /etc/hosts.allow    #设置允许指定ip连接服务器的ssh。
    // sshd:210.xx.xx.xx:allow
}
