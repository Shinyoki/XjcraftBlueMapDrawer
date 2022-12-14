# XJCraftBaseHomeBlueMapDrawer

## 描述

顾名难思义，这其实是一个为`XJCraft(小鸡服)`适配的插件，目前的作用只有一个（或许叫成Adapter更合适？），在`BlueMap(某卫星地图)`上渲染出已过审核玩家的`Home(小镇/家)`和`Base(基地/生存区域)`，还原以前还是`dynmap(另一款卫星地图插件)`时的使用体验。(虽然没有日出日落、网页聊天功能)

## 效果

- `小镇`和`基地`分开显示

> `小镇`的Marker默认不显示，防止预览地图时影响观察
>
> (可通过配置文件修改)。

![](https://gcore.jsdelivr.net/gh/Shinyoki/images_repository/blog_images/20220814145417.png)

# 配置文件

- `config.yml`

```yml
senko:
  debug-mode: false # 开启debug模式
  render:
    # 与XJcraftAudit中设置玩家命令方块的配置名保持一致
    town-cmdlocation: 'town-cmdlocation'
    base-cmdlocation: 'base-cmdlocation'
    # 自动显示marker
    default-show-home: false
    default-show-base: true
```

# 指令

![](https://gcore.jsdelivr.net/gh/Shinyoki/images_repository/blog_images/20220816081308.png)

# 需知

- 前置插件[AuditPlugin(小鸡服认证插件)](https://github.com/XJcraft/XJCraftAudit)和`BlueMap(某卫星地图插件)`。

> 兼容性：
>
> `BlueMap`：**^1.7.3**

# TODO

- 兼容`BlueMap2`版本

> 目前调用的`BlueMapAPI`版本为`1.7.0`，最近查资料时发现[BlueMap](https://github.com/BlueMap-Minecraft/BlueMap)出了`2.0`版本的API，而且破坏了很多`没标注@Deprecated`的接口，所以等以后`BlueMap`升级2.0版本后还得再进行适配。

- 摸鱼