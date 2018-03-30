# Prestige
Prestige

### 指令
```
管理员指令:需要<prestige.admin>权限
/prestige lang|save|reload|debug
/prestige add <player> <score> 给玩家<player>添加<score>点声望
/prestige set <player> <score> 设置玩家<player>的声望为<score>
/prestige open <world> 开启世界<world>的声望系统
/prestige close <world> 关闭世界<world>的声望系统
/prestige exec [player] 触发(某个)玩家等级指令
/prestuge clvl <score> 创建新的等级/段位
玩家指令:
/prestige info 查看自己的声望信息
/prestige top [page] 查看声望排行榜
```

### 配置
```yaml
# 调试模式
debug: false
# 显示语言
lang: zh_cn
# 声望计算公式
easyDieFormula: ($DeadScore$-$KillerScore$)*1.5
easyKillFormula: $KillerScore$/($KillerScore$-$DeadScore$)
simpleDieFormula: $KillerGradeScore$/100
simpleKillFormula: $DeadScore$/$KillerGradeScore$+1
difficultDieFormula: ($KillerScore$-$DeadScore$)/($DeadScore$/10)
difficultKillFormula: 2*($DeadScore$-$KillerScore$)
# 开启声望系统的世界
worlds:
- world
# 等级/段位配置
levels:
# 第一个等级/段位
- name: Level # 段位名称
  # 声望小于(不包含等于)此分数为此段位,大于等于为更高段位
  score: 10
  # 聊天栏显示 玩家名前缀,支持'&'颜色,中文需要使用Unicode编码
  prefix: "&6[\u9752\u94dc]&r[" # &6[青铜]&r[
  # 聊天栏显示 玩家名后缀,支持'&'颜色,中文需要使用Unicode编码
  suffix: ']'
  # 等级指令(顺序列表),当系统执行 prestige exec 指令时顺序执行,{player}可指代对应玩家名
  commands:
  - give {player} 1 1
# 第二个等级/段位
- name: Level2
  score: 30
  prefix: '&6['
  suffix: ']'
  commands:
  - tp {player} 0 100 0
  - give {player} 1 1
```
注意
1. 在中文windows环境下，如果需要在配置文件中使用中文，请转换文件编码为 GB2312，或者使用中文的Unicode代码.

### 权限
```yaml
# 执行主指令所需权限
permissions:
  prestige.admin:
    default: op
```
