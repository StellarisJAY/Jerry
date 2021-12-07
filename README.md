# Jerry
轻量级http服务器，它比Tomcat小，所以叫 Jerry。



## 快速入门

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

### 静态资源映射

想要让Jerry管理静态资源，比如html页面、图片？使用StaticResourceHandler可以轻松实现静态资源访问。

1. 将资源放在/resources/static/路径下。
2. 继承StaticResourceHandler，实现getResource方法，在方法中返回资源的相对路径。
3. 加上@Handler注解。

```java
@Handler("/image")
public class ImageHandler extends StaticResourceHandler {
    @Override
    public String getStaticResource(HttpRequest httpRequest) {
        return "timg.gif";
    }
}
```

![](https://images-1257369645.cos.ap-chengdu.myqcloud.com/jerry-docs/static-resource.PNG)

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



## 详细说明

### 获取form-data数据

Jerry会自动解析form-data的表单数据，并将属性存储在请求的参数列表中。

用户可以通过request.getParameter(name)获取表单数据。

```java
	@Override
    public void handlePost(HttpRequest request, HttpResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
		// do something...
    }
```

### 上传文件之MultiparFile

Jerry支持使用multipart-form-data方式的文件上传。

用户 可以通过 request.getFiles()获取文件列表。

获取到MultipartFile对象后，你可以通过它的InputStream来读取字节数据。

```java
	@Override
    public void handlePost(HttpRequest request, HttpResponse response) {
        
        List<MultipartFile> files = request.getFiles();
        
        for(MultipartFile file : files){
            // 获取文件名
            log.info("filename: {}", file.getFilename());
            // 获取form-data-name
            log.info("name: {}", file.getName());
            
            // read from inputStream
            InputStream inputStream = file.getInputStream();
            
        }
    }
```



### Jerry与Cookie

Jerry支持Cookie，你只需要用request.getCookie和response.setCookie就能够轻松的使用。

```java
	@Override
    public void handleGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // request cookie
        Cookie cookie = httpRequest.getCookie("my-cookie");
        log.info("myCookie is {}", cookie.getValue());

        // response cookie
        Cookie respCookie = Cookie.builder().name("response-cookie").value("jerry's cookie").build();
        httpResponse.setCookie(respCookie);
    }
```



### 使用Session

Jerry提供了Session功能，用户只需要使用request.getSession()就能获取一个会话对象。

目前Jerry的Session是基于Cookie的，所以暂时只支持开启Cookie的请求。

下面是一个简单的登录功能实例，可以帮你理解Jerry的Session：

```java
@Handler("/login")
public class LoginHandler extends DefaultHttpHandler {
    @Override
    public void handleGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    public void handlePost(HttpRequest request, HttpResponse response) {
        // 从form-data获取用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");
		
        // 封装用户对象
        User user = new User();
        user.setAge(100);
        user.setUsername(username);
        user.setPassword(password);
		
        // 省略：检查用户名密码
        
        // 获取session
        HttpSession session = request.getSession();
        // 将用户对象存入session
        session.put("login_user", user);

        response.out().write("login success");
    }
}
```

### 过滤器

想要过滤一些请求，Jerry的用户自定义过滤器可以很好地实现这个功能。

- 首先，创建过滤器类，继承AbstractFilter。
- 实现doFilter方法，方法返回true表示放行，返回false表示拦截。
- 创建构造方法，并调用父类构造方法。请注意，Jerry的IOC容器默认使用空参构造器，如果要使用其他构造方法，请在构造方法上添加@Construct注解，并对参数使用@Value赋值。
- 为过滤器类加上@Filter注解，它会告诉Jerry这是一个过滤器，需要存放在IOC容器中。

下面是一个用户登录验证的过滤器示例：

```java
@Filter
@Slf4j
public class LoginFilter extends AbstractFilter {
    public LoginFilter() {
        /*
        三个参数分别是，过滤器名称，优先级，排除路径规则
        优先级数值大的过滤器优先执行。
        排除路径请使用正则表达式声明。
        */
        super("login-filter", 1000, new String[]{"/login"});
    }

    @Override
    public boolean doFilter(HttpRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        return session.get("login_user") != null;
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



### 关于Jerry的性能

目前Jerry还处于开发阶段，所以没有实际应用的性能测试报告。不过在开发过程中，开发者有使用Jmeter对Jerry进行简单的压力测试。

#### 测试代码

一个最简单的handler，它接收到请求后直接回复一个hello字符串给客户端。

```java
@Handler("/hello")
@Slf4j
public class HelloHandler extends DefaultHttpHandler {

    @Override
    public void handleGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.out().write("hello");
    }

    @Override
    public void handlePost(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.out().write("hello");
    }
}
```

#### 测试环境

- CPU：Intel Core i7-8750H 2.20GHz
- JMeter线程数：1000
- 线程循环次数：100
- 测试次数：3

#### 测试结果

![](https://images-1257369645.cos.ap-chengdu.myqcloud.com/jerry-docs/%E6%B5%8B%E8%AF%95%E6%8A%A5%E5%91%8A/jerry-test-1.PNG)



![](https://images-1257369645.cos.ap-chengdu.myqcloud.com/jerry-docs/%E6%B5%8B%E8%AF%95%E6%8A%A5%E5%91%8A/jerry-test-2.PNG)



![](https://images-1257369645.cos.ap-chengdu.myqcloud.com/jerry-docs/%E6%B5%8B%E8%AF%95%E6%8A%A5%E5%91%8A/jerry-test-3.PNG)
