# RestTemplate 
## 依赖注入
> 可以每次都自己new，但是没必要   

```
@Bean
// 开启负载均衡
@LoadBalanced
public RestTemplate getRestTemplate() {
    return new RestTemplate();
}
```
## Get 请求
1.getForEntity   
> getForEntity方法的返回值是一个ResponseEntity，
>ResponseEntity是Spring对HTTP请求响应的封装，
>包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等。
```
<200,Hi,[Content-Type:"text/plain;charset=UTF-8", Content-Length:"8", Date:"Fri, 10 Apr 2020 09:58:44 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]>
```
1.1返回一个Map   
1.1.1调用方   
```
String url ="http://provider/getMap";	   
ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);
System.out.println("respStr: "  + entity.getBody() );
```
1.1.2生产方   
```
@GetMapping("/getMap")
public Map<String, String> getMap() {
    
    HashMap<String, String> map = new HashMap<>();
    map.put("name", "500");
    return map; 
}
```
1.2返回对象   
1.2.1调用方   
```
ResponseEntity<Person> entity = restTemplate.getForEntity(url, Person.class);	   
System.out.println("respStr: "  + ToStringBuilder.reflectionToString(entity.getBody() ));
```
1.2.2生产方   
```
@GetMapping("/getObj")
public Person getObj() {
    Person person = new Person();
    person.setId(100);
    person.setName("xiaoming");
    return person; 
}
```
1.3传参调用   
1.3.1使用占位符    
```
String url ="http://provider/getObjParam?name={1}";   
ResponseEntity<Person> entity = restTemplate.getForEntity(url, Person.class,"hehehe...");
```
1.3.2使用map   
```
String url ="http://provider/getObjParam?name={name}";	   
Map<String, String> map = Collections.singletonMap("name", " memeda");
ResponseEntity<Person> entity = restTemplate.getForEntity(url, Person.class,map);
```
2.getForObject   
```
String url ="http://provider/getObjParam?name={name}";	   
Map<String, String> map = Collections.singletonMap("name", " memeda");
Person person = restTemplate.getForObject(url, Person.class,map);
```

## Post 请求
1.postForEntity    
1.1调用方   
```
String url ="http://provider/postParam";	   
Map<String, String> map = Collections.singletonMap("name", " memeda");
ResponseEntity<Person> entity = restTemplate.postForEntity(url, map, Person.class);
```
1.2生产方   
```
@PostMapping("/postParam")
public Person postParam(@RequestBody String name) {
    System.out.println("name:" + name);
    Person person = new Person();
    person.setId(100);
    person.setName("xiaoming" + name);
    return person; 
}
```
2.postForLocation   
2.1调用方   
```
String url ="http://provider/postParam";   
Map<String, String> map = Collections.singletonMap("name", " memeda");
URI location = restTemplate.postForLocation(url, map, Person.class);
System.out.println(location);
```
2.2生产方   
需要设置头信息，不然返回的是null   
```
public URI postParam(@RequestBody Person person,HttpServletResponse response) throws Exception {
    URI uri = new URI("https://www.baidu.com/s?wd="+person.getName());
    response.addHeader("Location", uri.toString());
    return uri;
}
```   

## 拦截器  
实现```ClientHttpRequestInterceptor```接口   
```
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

@Override
public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {

    System.out.println("拦截啦！！！");
    System.out.println(request.getURI());

    ClientHttpResponse response = execution.execute(request, body);

    System.out.println(response.getHeaders());
    return response;
}
```
添加到restTemplate中   
```
@Bean
@LoadBalanced
RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    return restTemplate;
}
```