# XJCraftBaseHomeBlueMapDrawer

## 描述

顾名难思义，这其实是一个为`XJCraft(小鸡服)`适配的插件，目前支持在`BlueMap(某卫星地图)`上渲染出已过审核玩家的`Home(小镇/家)`和`Base(基地/生存区域)`，以及通过`/xjb [show | hide]`来显示或隐藏自己在线时的玩家图标，还原以前还在使用`dynmap(另一款卫星地图插件)`时的使用体验。(虽然没有日出日落、网页聊天功能)


## 效果

- `小镇`和`基地`分开显示

> `小镇`的Marker默认不显示，防止预览地图时影响观察
>
> (可通过配置文件修改)。

![](https://gcore.jsdelivr.net/gh/Shinyoki/images_repository/blog_images/20220814145417.png)

- 在线玩家位置动态显示
> 玩家可以输入 `/xjb [show | hide]` 来决定是否让自己的位置显示在地图上。
> 
![](https://gcore.jsdelivr.net/gh/Shinyoki/images_repository/blog_images/20230205210005.png)

# 配置文件

- `config.yml`

```yml
senko:
  debug-mode: false # 开启debug模式
  render:
    # 自动显示marker
    default-show-home: false
    default-show-base: true
    # 开启在线玩家Marker渲染
    enable-online-player-marker-render: true
  xjcraft:
    # 与XJcraftAudit中设置玩家命令方块的配置名保持一致
    town-cmdlocation: 'town-cmdlocation'
    base-cmdlocation: 'base-cmdlocation'
    # 存放玩家认证信息文件的所在文件夹相对路径 如 status
    apply-player-meta-path: 'status'
    # 服务器地图名称，如MainLand
    server-map-name: 'MainLand'
```

# 指令

![](https://gcore.jsdelivr.net/gh/Shinyoki/images_repository/blog_images/20230205210404.png)

# 需知

- 前置插件[AuditPlugin(小鸡服认证插件)](https://github.com/XJcraft/XJCraftAudit)和`BlueMap(某卫星地图插件)`。
- 不同版本里插件配置存在变动，需要提前删除旧的配置文件，否则部分功能会沿用默认配置。

> 兼容性：
>
> `BlueMap`：**^3.9**<br/>
> `BlueMap-API`: **^2.4.0**

# TODO

- 摸鱼