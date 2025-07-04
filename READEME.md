# AutoMybatisGenerator

一个自动生成原生Mybatis代码（基本增删改查，模糊查询，分页查询）的工具，仅需修改配置文件就能自动生成。

## 1. 特性
- 支持数据库表结构自动识别
- 自动生成实体类（PO）、查询对象（Query）、Mapper、Mapper XML、Service、Controller 等
- 支持模糊查询、分页查询、批量操作
- 代码风格统一，注释齐全
- 只需修改配置文件，无需手动编写模板

## 2. 快速开始  
### 1. 克隆项目  
```bash
git clone https://github.com/Skyl234/AutoMybatisGenerator.git
cd AutoMybatis/Generator
```
### 2. 配置数据库和生成参数
```properties
db.driver.name=驱动
db.url=驱动url
db.username=
db.password=

#是否忽略表前缀（比如tb_xxx）
ignore.table.prefix=false
#需要忽略的属性,用逗号隔开，请确保属性唯一性
ignore.bean.tojson.field=password
#忽略的表达式（注解）
ignore.bean.tojson.expression=@JsonIgnore
ignore.bean.tojson.class=import com.fasterxml.jackson.annotation.JsonIgnore;

#日期格式序列化（可自定义实现）
bean.date.format.expression=@JsonFormat(pattern = "%s", timezone = "GMT + 8")
bean.date.format.class=import com.fasterxml.jackson.annotation.JsonFormat;

#日期格式反序列化（可自定义实现）
bean.date.parsejson.expression=@DateTimeFormat(pattern = "%s")
bean.date.parsejson.class=import org.springframework.format.annotation.DateTimeFormat;

#搜索参数列bean后缀
suffix.bean.param=Query
#参数模糊搜索后缀
suffix.bean.param.fuzzy=Fuzzy
#参数日期起止
suffix.bean.param.time.start=Start
suffix.bean.param.time.end=End
#mapper后缀
suffix.mappers=Mapper
#文件输出路径
path.base=
#包名
package.base=com.xxx
package.pojo=entity.po
package.param=entity.query
package.mappers=mappers
package.enums=entity.enums
package.vo=entity.vo
package.service=service
package.service.impl=service.impl
package.exception=exception
package.controller=controller
```

### 3.运行  
确保环境正确的情况下运行RunApplication.java

## 3. 目录结构  
* src/main/resources/template/：代码模板
* src/main/resources/application.properties：配置文件
* src/main/java/com/AutoMybatis/：核心代码生成逻辑

## 4. 注意事项  
* 推荐实际开发目录与生成目录分离，确保生成时
不会覆盖您新写的代码。
* 如果是WEB项目，建议自己先搭建好，工具生成的
代码不会被IDE自动识别，需要手动配置。
* 目前该项目只支持Mysql数据库。

## 5. 贡献  
欢迎提交 issue 和 PR！
