---
title: readme.md
author: weizuxiao
---

# 开启redis过期key事件通知

```bash

# 开启key过期事件通知，当key到期时，会生成expire事件；
# K: 键空间通知，所有通知都以__keyspace@<db>__为前缀
# E: 键事件通知，所有通知都以__keyevent@<db>__为前缀
# x: 过期事件，每当有过期键被删除时发送
# A: 参数g$lshzxe的别名
# AKE: 表示发送所有类型的通知

redis-cli config set notify-keyspace-events Ex

```