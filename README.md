# Json
java对象和json数据之间的转换方式一般有两种，一种是引用第三方的jar包，如Gson（谷歌）、Fastjson（阿里）、Jackson等，这种方式优点是语法精练，可以实现一句话转化，但缺点是会引入庞大的第三方库，第二种是直接使用Java自带的org.json解析，但这个库功能比较基础，解析会写很多重复的代码，这篇文章在原生json的基础上做了一些包装，也实现了一句话转化。

序列化（toJson）的实现思路是：通过反射找到对象中所有的属性和对应的属性值，然后根据属性的类型调用org.json相应的方法将值装入进去，遇到数组的时候，通过递归层层转化，然后使用原生的方法将对象转变为json数据。

调用过程：
class test{
	int id;
	String msg;
	boolean error;
}
test a=new test();
a.id=10001;
a.msg="JAVABean转化为JSON~";
a.error=true;

try {
	System.out.println(Json.toJson(a));
} catch (Exception e) {
	e.printStackTrace();
}

输出结果：
{"msg":"JAVABean转化为JSON~","id":10001,"error":true}


当然我们也可以解析JSON数组和对象
JSONArray用java的List来解析
JSONObject用Java的Map来解析

class test{
	int id;
	String msg;
	boolean error;
	HashMap<String,Object> map;
	List<Object> list;
}

test a=new test();
a.id=10001;
a.msg="JAVABean转化为JSON~";
a.error=true;
a.map=new HashMap<String,Object>();
a.list=new ArrayList<Object>();

a.list.add("array1");
a.list.add("array2");

a.map.put("name", "yiqinlin");
a.map.put("mail", "yiqinlin@foxamil.com");

try {
    System.out.println(Json.toJson(a));
} catch (Exception e) {
    e.printStackTrace();
}

输出结果：
{
    "msg":"JAVABean转化为JSON~",
    "id":10001,
    "error":true,
    "list":["array1","array2"],
    "map":{
    "mail":"yiqinlin@foxamil.com",
    "name":"yiqinlin"
    }
}

反序列化（fromJson）的实现思路是：利用JAVA的反射来找出对象中包含的属性，然后通过属性名使用org.json提取json字符串中对应的属性值，再把值通过反射赋值给对象中的属性。

调用过程：
String jsonStr="{\"msg\":\"JSON转化为JAVABean~\",\"id\":10001,\"error\":true}";
try {
    test a=Json.fromJson(jsonStr,test.class);
    System.out.println(a.id+"--"+a.msg+"--"+a.error);
} catch (Exception e) {
    e.printStackTrace();
}

输出结果：
10001--JSON转化为JAVABean~--true

解析数组和对象

String jsonStr="{"
	+ "\"msg\":\"JAVABean转化为JSON~\","
	+ "\"id\":10001,"
	+ "\"error\":true,"
	+ "\"list\":[\"array1\",\"array2\"],"
	+ "\"map\":{\"mail\":\"yiqinlin@foxamil.com\",\"name\":\"yiqinlin\"}}";
try {
    test a=Json.fromJson(jsonStr,test.class);
    System.out.println(a.id+"--"+a.msg+"--"+a.error+"--"+a.list.get(0)+"--"+a.map.get("name"));
} catch (Exception e) {
    e.printStackTrace();
}
输出结果

10001--JAVABean转化为JSON~--true--array1--yiqinlin
