# Jerry
轻量级http服务器，它比Tomcat小，所以叫Jerry。



## 使用说明

### 导入依赖

```xml-dtd
	<dependencies>
        <dependency>
            <groupId>com.jay</groupId>
            <artifactId>jerry</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

### 配置文件

在resources目录下创建server.properties，并添加以下配置。

```properties
# 服务器端口
server.port=8080
```

### 创建Handler

Handler类似Tomcat的Servlet和SpringMVC的Controller，它负责处理请求。

创建一个Handler很简单：

1. 继承DefaultHttpHandler，实现GET和POST方法。
2. 添加@Handler注解，并在注解中指定请求路径。

```java
@Handler("/hello")
@Slf4j
public class HelloHandler extends DefaultHttpHandler {

    @Override
    public void handleGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 获取参数
        Map<String, String> params = httpRequest.getParams();
        String name = params.get("name");
        
        log.info("hello: {}", name);
        // 使用out写入数据
        httpResponse.out().write("hello: " + name);
    }

    @Override
    public void handlePost(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 处理POST
		httpResponse.out().write("hello");
    }
}
```

### 启动Jerry服务器

通过JerryApplication.run方法开启Jerry服务器，不用担心tomcat那样的servlet配置文件，Jerry会自动扫描标有@Handler注解的处理器，并保存在自己的IOC容器中。

不要忘了使用@IOCScan声明要扫描的包。

```java
@IOCScan(basePackage = "com.jay.test.handler")
public class TestApplication {
    public static void main(String[] args) {
        JerryApplication.run(TestApplication.class, args);
    }
}
```

### 想使用Jerry的IOC容器？

你可以通过@IOC注解来声明一个被Jerry管理的单例对象，并通过@Value注解对属性赋值。

```java
@IOC
public class User {
   	// Jerry 会从配置文件读取属性值
    @Value("user.name")
    private String name;
    @Value("user.age")
    private int age;
    
    private String email;
    
    // 通过@Construct告诉Jerry你想使用的构造方法
    @Construct
    public User(@Value("user.email") String email){
        this.email = email;
    }
    
    // 没有想用的构造方法？Jerry会默认使用空参构造方法，不过记得使用@Value给参数赋值
    public User(){
        
    }
}
```

