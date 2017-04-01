package com.pulin.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * 将json字符串转换为对应的javabean
 * @author Administrator
 *
 */
public class JsonUtils {

		/**
		 * main
		 * @param args
		 */
	 	public static void main(String[] args) {
	        String outPath = System.getProperty("user.dir")+File.separator+"output";
	        File jsonFile =  new File("D:\\code_pulin\\pulin\\src\\main\\java\\com\\pulin\\utils\\tt.txt");
	 		String json = FileUtils.readToString(jsonFile, "UTF-8");
	        parseJson2Java(json, outPath);
	    }

	    /**
	     * 将json字符串转换为对应的javabean
	     * <p>
	     * <p>
	     * 用法:<br>
	     * 将json字符串拷贝至本项目中/Json/JsonString.txt 文件中去,然后调用该方法,<br>
	     * 就会在本项目中/Json/JsonBean.java中生成一个对应的JavaBean类<br><br>
	     * 注意:<br>
	     * 如果json字符串中有null或者空集合[]这种无法判断类型的,会统一使用Object类型
	     */
	    public static void parseJson2Java(String jsonStr,String outPath) {
	        // 解析获取整个json结构集合
	        List<Json2JavaElement> jsonBeanTree = getJsonBeanTree(jsonStr);

	        // 利用获取到的json结构集合,创建对应的javabean文件内容
	        String javaBeanStr = createJavaBean(jsonBeanTree);

	        // 将生成的内容写入到文件中去
	        File f = new File(outPath);
	        if(!f.exists()){
	        	try {
	        		f.mkdirs();		
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        
	        File file = new File(outPath,"JavaBean"+System.currentTimeMillis()+".java");
	        if(!file.exists()){
	        	try {
	        		file.createNewFile();		
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        
	        FileUtils.writeString2File(javaBeanStr, file);
	    }

	    /**
	     * 将json字符串转换为对应的javabean
	     *
	     * @return 生成的javabean代码
	     */
	    public static String getJavaFromJson(String jsonStr) {
	        // 解析获取整个json结构集合
	        List<Json2JavaElement> jsonBeanTree = getJsonBeanTree(jsonStr);

	        // 利用获取到的json结构集合,创建对应的javabean文件内容
	        String javaBeanStr = createJavaBean(jsonBeanTree);

	        return javaBeanStr;
	    }

	    /**
	     * 根据解析好的数据创建生成对应的javabean类字符串
	     *
	     * @param jsonBeanTree 解析好的数据集合
	     * @return 生成的javabean类字符串
	     */
	    public static String createJavaBean(List<Json2JavaElement> jsonBeanTree) {
	        StringBuilder sb = new StringBuilder();
	        StringBuilder sbGetterAndSetter = new StringBuilder();
	        sb.append("\n");

	        // 是否包含自定义子类
	        boolean hasCustomeClass = false;
	        List<String> customClassNames = new ArrayList<String>();

	        // 由于在循环的时候有移除操作,所以使用迭代器遍历
	        Iterator<Json2JavaElement> iterator = jsonBeanTree.iterator();
	        while (iterator.hasNext()) {
	            Json2JavaElement j2j = iterator.next();
	            // 保存自定义类名称至集合中,注意已经包含的不再添加
	            if (j2j.getCustomClassName() != null && !customClassNames.contains(j2j.getCustomClassName())) {
	            	
	            	String k = j2j.getCustomClassName();
	            	String[] kk = k.split("_");
	            	if(kk != null && kk.length > 1){
	            		StringBuffer sbkk = new StringBuffer(kk[0]);
	                	for(int i=1;i<kk.length;i++){
	                		String temp = kk[i];
	                		sbkk.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
	                	}
	                	//System.out.println(sbkk.toString());
	                	j2j.setCustomClassName(sbkk.toString());
	            	}
	                customClassNames.add(j2j.getCustomClassName());
	            }

	            if (j2j.getParentJb() != null) {
	                // 如果有parent,则为自定义子类,设置标识符不做其他操作
	                hasCustomeClass = true;
	            } else {
	                // 如果不是自定义子类,则根据类型名和控件对象名生成变量申明语句
	                genFieldd(sb, sbGetterAndSetter, j2j, 0);
	                // 已经使用的数据会移除,则集合中只会剩下自定义子类相关的元素数据,将在后续的循环中处理
	                iterator.remove();
	            }
	        }

	        // 设置所有自定义类
	        if (hasCustomeClass) {
	            for (String customClassName : customClassNames) {
	                // 根据名称申明子类
	                sb.append("\n");
	                sb.append(StringUtils.formatSingleLine(1, "public static class " + customClassName + " {"));

	                StringBuilder sbSubGetterAndSetter = new StringBuilder();
	                // 循环余下的集合
	                Iterator<Json2JavaElement> customIterator = jsonBeanTree.iterator();
	               
	                while (customIterator.hasNext()) {
	                    Json2JavaElement j2j = customIterator.next();
	                    
	                    // 根据当前数据的parent名称,首字母转为大写生成parent的类名
	                    String parentClassName = StringUtils.firstToUpperCase(j2j.getParentJb().getName());
	                  
	                    if(parentClassName.indexOf("_")>0){
	                    	StringBuffer sbArr = new StringBuffer();
	                    	String[] parentClassNameeArr= parentClassName.split("_");
	                    	sbArr.append(parentClassNameeArr[0]);
	                    	for(int i=1;i<parentClassNameeArr.length;i++){
	                    		String name = parentClassNameeArr[i];
	                    		sbArr.append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
	                    	}
	                    	parentClassName = sbArr.toString();
	                    }
	                    
	                    
	                    // 如果当前数据属于本次外层循环需要处理的子类
	                    if (parentClassName.equals(customClassName)) {
	                        // 根据类型名和控件对象名生成变量申明语句
	                        genFieldd(sb, sbSubGetterAndSetter, j2j, 1);

	                        // 已经使用的数据会移除,减少下一次外层循环的遍历次数
	                        customIterator.remove();
	                    }
	                }

	                sb.append(sbSubGetterAndSetter.toString());
	                sb.append(StringUtils.formatSingleLine(1, "}"));
	            }
	        }

	        sb.append(sbGetterAndSetter.toString());
	        sb.append("\n");
	        return sb.toString();
	    }

	    /**
	     * 生成变量相关代码
	     *
	     * @param sb                添加申明变量部分
	     * @param sbGetterAndSetter 添加getter和setter方法部分
	     * @param j2j               变量信息
	     * @param extraTabNum       额外缩进量\t
	     */
	    private static void genFieldd(StringBuilder sb, StringBuilder sbGetterAndSetter,Json2JavaElement j2j, int extraTabNum) {
	        // 先判断是否有注释,有的话添加之
	        // /**
	        //  * 姓名
	        //  */
	        String des = j2j.getDes();
	        
	        
	       String[] kk =  j2j.getName().split("_");
	       String serializedName = null;
	       if(kk!= null && kk.length>1){
	    	   StringBuffer kksb = new StringBuffer(kk[0]);
	    	   for(int i=1;i<kk.length;i++){
	    		   String temp=kk[i];
	    		   kksb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
	    	   }
//	    	   System.out.println(j2j.getName());
//	    	   System.out.println(kksb.toString());
//	    	   System.out.println("--------------------");
	    	   serializedName = j2j.getName();
	    	   j2j.setName(kksb.toString());
	       }
	        
	        
	        if (des != null && des.length() > 0) {
	            sb.append(StringUtils.formatSingleLine(1 + extraTabNum, "/**"));
	            sb.append(StringUtils.formatSingleLine(1 + extraTabNum, " * " + des));
	            sb.append(StringUtils.formatSingleLine(1 + extraTabNum, " */"));
	        }

	        // 申明变量
	        // private String name;
	        if(serializedName != null){
	        	sb.append( StringUtils.formatSingleLine(1 + extraTabNum,"@SerializedName(\""+serializedName+"\")") );
	        }
	        sb.append(StringUtils.formatSingleLine(1 + extraTabNum,"private " + getTypeName(j2j) + " " + j2j.getName() + ";"));

	        // 生成变量对应的getter和setter方法
	        // public String getName() {
	        //     return name;
	        // }
	        sbGetterAndSetter.append("\n");
	        sbGetterAndSetter.append(
	        	StringUtils.formatSingleLine(1 + extraTabNum,"public " + getTypeName(j2j) + " get" + StringUtils.firstToUpperCase(j2j.getName()) + "() {")
	        );
	        sbGetterAndSetter.append( StringUtils.formatSingleLine(2 + extraTabNum, "return " + j2j.getName() + ";") );
	        sbGetterAndSetter.append( StringUtils.formatSingleLine(1 + extraTabNum, "}") );

	        // public void setName(String name) {
	        //     this.name = name;
	        // }
	        sbGetterAndSetter.append("\n");
	        sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum,
	                "public void set" + StringUtils.firstToUpperCase(j2j.getName()) +
	                        "(" + getTypeName(j2j) + " " + j2j.getName() + ") {"));
	        
	        sbGetterAndSetter.append(StringUtils.formatSingleLine(2 + extraTabNum,
	                "this." + j2j.getName() + " = " + j2j.getName() + ";"));
	        
	        sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum, "}"));
	        
	    }

	    /**
	     * 递归遍历整个json数据结构,保存至jsonBeans集合中
	     *
	     * @param jsonStr json字符串
	     * @return 解析好的数据集合
	     */
	    public static List<Json2JavaElement> getJsonBeanTree(String jsonStr) {
	        JsonParser parser = new JsonParser();
	        JsonElement element = parser.parse(jsonStr);

	        // 根element可能是对象也可能是数组
	        JsonObject rootJo = null;
	        if (element.isJsonObject()) {
	            rootJo = element.getAsJsonObject();
	        } else if (element.isJsonArray()) {
	            // 集合中如果有数据,则取第一个解析
	            JsonArray jsonArray = element.getAsJsonArray();
	            if (jsonArray.size() > 0) {
	                rootJo = jsonArray.get(0).getAsJsonObject();
	            }
	        }

	        jsonBeans = new ArrayList<Json2JavaElement>();
	        recursionJson(rootJo, null);
	        return jsonBeans;
	    }

	    /**
	     * 保存递归获取到数据的集合
	     */
	    private static List<Json2JavaElement> jsonBeans = new ArrayList<Json2JavaElement>();

	    /**
	     * 递归获取json数据
	     *
	     * @param jo     当前递归解析的json对象
	     * @param parent 已经解析好的上一级数据,无上一级时传入null
	     */
	    private static void recursionJson(JsonObject jo, Json2JavaElement parent) {
	        if (jo == null) {
	            return;
	        }

	        // 循环整个json对象的键值对
	        for (Entry<String, JsonElement> entry : jo.entrySet()) {
	            // json对象的键值对建构为 {"key":value}
	            // 其中,值可能是基础类型,也可能是集合或者对象,先解析为json元素
	            String name = entry.getKey();
	            JsonElement je = entry.getValue();

	            Json2JavaElement j2j = new Json2JavaElement();
	            j2j.setName(name);
	            if (parent != null) {
	                j2j.setParentJb(parent);
	            }

	            // 获取json元素的类型,可能为多种情况,如下
	            Class<?> type = getJsonType(je);
	            if (type == null) {
	                // 自定义类型

	                // json键值的首字母转为大写,作为自定义类名
	                j2j.setCustomClassName(StringUtils.firstToUpperCase(name));
	                // ?
	                j2j.setSouceJo(je.getAsJsonObject());
	                jsonBeans.add(j2j);

	                // 自定义类需要继续递归,解析自定义类中的json结构
	                recursionJson(je.getAsJsonObject(), j2j);
	            } else if (type.equals(JsonArray.class)) {
	                // 集合类型

	                // 重置集合数据,并获取当前json元素的集合类型信息
	                deepLevel = 0;
	                arrayType = new ArrayType();
	                getJsonArrayType(je.getAsJsonArray());

	                j2j.setArray(true);
	                j2j.setArrayDeep(deepLevel);

	                if (arrayType.getJo() != null) {
	                    j2j.setCustomClassName(StringUtils.firstToUpperCase(name));
	                    // 集合内的末点元素类型为自定义类, 递归
	                    recursionJson(arrayType.getJo(), j2j);
	                } else {
	                    j2j.setType(arrayType.getType());
	                }
	                jsonBeans.add(j2j);
	            } else {
	                // 其他情况,一般都是String,int等基础数据类型

	                j2j.setType(type);
	                jsonBeans.add(j2j);
	            }
	        }
	    }

	    /**
	     * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
	     */
	    private static int deepLevel = 0;
	    /**
	     * 集合类型数据,用于保存递归获取到的集合信息
	     */
	    private static ArrayType arrayType = new ArrayType();

	    /**
	     * 递归获取集合的深度和类型等信息
	     *
	     * @param jsonArray json集合数据
	     */
	    private static void getJsonArrayType(JsonArray jsonArray) {
	        // 每次递归,集合深度+1
	        deepLevel++;

	        if (jsonArray.size() == 0) {
	            // 如果集合为空,则集合内元素类型无法判断,直接设为Object
	            arrayType.setArrayDeep(deepLevel);
	            arrayType.setType(Object.class);
	        } else {
	            // 如果集合非空则取出第一个元素进行判断
	            JsonElement childJe = jsonArray.get(0);

	            // 获取json元素的类型
	            Class<?> type = getJsonType(childJe);

	            if (type == null) {
	                // 自定义类型

	                // 设置整个json对象,用于后续进行进一步解析处理
	                arrayType.setJo(childJe.getAsJsonObject());
	                arrayType.setArrayDeep(deepLevel);
	            } else if (type.equals(JsonArray.class)) {
	                // 集合类型

	                // 如果集合里面还是集合,则递归本方法
	                getJsonArrayType(childJe.getAsJsonArray());
	            } else {
	                // 其他情况,一般都是String,int等基础数据类型

	                arrayType.setArrayDeep(deepLevel);
	                arrayType.setType(type);
	            }
	        }
	    }

	    /**
	     * 获取json元素的类型
	     *
	     * @param je json元素
	     * @return 类型
	     */
	    private static Class<?> getJsonType(JsonElement je) {
	        Class<?> clazz = null;

	        if (je.isJsonNull()) {
	            // 数据为null时,无法获取类型,则视为object类型
	            clazz = Object.class;
	        } else if (je.isJsonPrimitive()) {
	            // primitive类型为基础数据类型,如String,int等
	            clazz = getJsonPrimitiveType(je);
	        } else if (je.isJsonObject()) {
	            // 自定义类型参数则返回null,让json的解析递归进行进一步处理
	            clazz = null;
	        } else if (je.isJsonArray()) {
	            // json集合类型
	            clazz = JsonArray.class;
	        }
	        return clazz;
	    }

	    /**
	     * 将json元素中的json基础类型,转换为String.class,int.class等具体的类型
	     *
	     * @param je json元素
	     * @return 具体数据类型, 无法预估的类型统一视为Object.class类型
	     */
	    private static Class<?> getJsonPrimitiveType(JsonElement je) {
	        Class<?> clazz = Object.class;
	        JsonPrimitive jp = je.getAsJsonPrimitive();
	        // json中的类型会将数字集合成一个总的number类型,需要分别判断
	        if (jp.isNumber()) {
	            String num = jp.getAsString();
	            if (num.contains(".")) {
	                // 如果包含"."则为小数,先尝试解析成float,如果失败则视为double
	                try {
	                    Float.parseFloat(num);
	                    clazz = Float.class;
	                } catch (NumberFormatException e) {
	                    clazz = Double.class;
	                }
	            } else {
	                // 如果不包含"."则为整数,先尝试解析成int,如果失败则视为long
	                try {
	                	
	                	if(num.length()>9){
	                		Long.parseLong(num);
	 	                    clazz = Long.class;
	                	}else{
	                		Integer.parseInt(num);
	 	                    clazz = Integer.class;
	                	}
	                   
	                } catch (NumberFormatException e) {
	                    clazz = Long.class;
	                }
	            }
	        } else if (jp.isBoolean()) {
	            clazz = Boolean.class;
	        } else if (jp.isString()) {
	            clazz = String.class;
	        }
	        // json中没有其他具体类型如byte等
	        return clazz;
	    }

	    /**
	     * 获取类型名称字符串
	     *
	     * @param j2j 转换数据元素
	     * @return 类型名称, 无法获取时, 默认Object
	     */
	    private static String getTypeName(Json2JavaElement j2j) {
	        String name = "Object";

	        Class<?> type = j2j.getType();
	        if (j2j.getCustomClassName() != null && j2j.getCustomClassName().length() > 0) {
	            // 自定义类,直接用自定义的名称customClassName
	            name = j2j.getCustomClassName();
	        } else {
	            // 非自定义类即可以获取类型,解析类型class的名称,如String.class就对应String
	            name = type.getName();
	            int lastIndexOf = name.lastIndexOf(".");
	            if (lastIndexOf != -1) {
	                name = name.substring(lastIndexOf + 1);
	            }
	        }

	        // 如果集合深度大于0,则为集合数据,根据深度进行ArrayList嵌套
	        // 深度为3就是ArrayList<ArrayList<ArrayList<type>>>
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < j2j.getArrayDeep(); i++) {
	            sb.append("ArrayList<");
	        }
	        sb.append(name);
	        for (int i = 0; i < j2j.getArrayDeep(); i++) {
	            sb.append(">");
	        }
	       // System.out.println(sb.toString());
	        return sb.toString();
	    }
	    
	    
	    
    
    
    
    
    
    /**
     * 转换数据元素
     */
    static class Json2JavaElement {

    	/**
    	 * 是否为集合类型
    	 * 
    	 * <p>
    	 * 如果是集合的话,集合内数据类型为customClassName对应的自定义类,或者type
    	 */
    	private boolean isArray;
    	
    	/**
    	 * 集合数据
    	 */
    	private JsonElement arrayItemJe;
    	
    	/**
    	 * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
    	 */
    	private int arrayDeep;

    	/**
    	 * 自定义类名
    	 * 
    	 * <p>
    	 * 非空时代表是自定义类,此时不使用type参数(customClassName和type只能二选一,互斥关系)
    	 */
    	private String customClassName;
    	private JsonObject souceJo;
    	private Json2JavaElement parentJb;

    	private String name;
    	private Class<?> type;
    	
    	/**
    	 * 注释,null时不添加注释
    	 */
    	private String des;

    	public boolean isArray() {
    		return isArray;
    	}

    	public void setArray(boolean isArray) {
    		this.isArray = isArray;
    	}

    	public JsonElement getArrayItemJe() {
    		return arrayItemJe;
    	}

    	public void setArrayItemJe(JsonElement arrayItemJe) {
    		this.arrayItemJe = arrayItemJe;
    	}

    	public int getArrayDeep() {
    		return arrayDeep;
    	}

    	public void setArrayDeep(int arrayDeep) {
    		this.arrayDeep = arrayDeep;
    	}

    	public String getCustomClassName() {
    		return customClassName;
    	}

    	public void setCustomClassName(String customClassName) {
    		this.customClassName = customClassName;
    	}

    	public JsonObject getSouceJo() {
    		return souceJo;
    	}

    	public void setSouceJo(JsonObject souceJo) {
    		this.souceJo = souceJo;
    	}

    	public Json2JavaElement getParentJb() {
    		return parentJb;
    	}

    	public void setParentJb(Json2JavaElement parentJb) {
    		this.parentJb = parentJb;
    	}

    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}

    	public Class<?> getType() {
    		return type;
    	}

    	public void setType(Class<?> type) {
    		this.type = type;
    	}
    	
    	public String getDes() {
    		return des;
    	}

    	public void setDes(String des) {
    		this.des = des;
    	}

    	@Override
    	public String toString() {
    		return "\n"
    				+ "Json2JavaElement [isArray=" + isArray
    				+ ", arrayDeep=" + arrayDeep + ", name=" + name + ", type="
    				+ type + "]";
    	}

    }
    
    /**
     * 集合类型数据
     */
    static class ArrayType {
    	/**
    	 * 集合中泛型的类型
    	 */
    	private Class<?> type;
    	/**
    	 * 如果集合泛型为自定义类型,用此参数保存数据
    	 */
    	private JsonObject jo;
    	/**
    	 * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
    	 */
    	private int arrayDeep;

    	public Class<?> getType() {
    		return type;
    	}

    	public void setType(Class<?> type) {
    		this.type = type;
    	}

    	public JsonObject getJo() {
    		return jo;
    	}

    	public void setJo(JsonObject jo) {
    		this.jo = jo;
    	}

    	public int getArrayDeep() {
    		return arrayDeep;
    	}

    	public void setArrayDeep(int arrayDeep) {
    		this.arrayDeep = arrayDeep;
    	}

    }
    
    
    
    
    static class StringUtils {
    	
    	/**
    	 * 将string按需要格式化,前面加缩进符,后面加换行符
    	 * @param tabNum 缩进量
    	 * @param srcString
    	 * @return
    	 */
    	public static String formatSingleLine(int tabNum, String srcString) {
    		StringBuilder sb = new StringBuilder();
    		for(int i=0; i<tabNum; i++) {
    			sb.append("\t");
    		}
    		sb.append(srcString);
    		sb.append("\n");
    		return sb.toString();
    	}
    	
    	public static String firstToUpperCase(String key) {
    		return key.substring(0, 1).toUpperCase(Locale.CHINA) + key.substring(1);
    	}
    	
    	public static String gapToCamel(String src) {
    		StringBuilder sb = new StringBuilder();
    		for(String s : src.trim().split(" ")) {
    			sb.append(firstToUpperCase(s));
    		}
    		return sb.toString();
    	}

    	/**
    	 * 驼峰转下划线命名
         */
    	public static String camelTo_(String src) {
    		StringBuilder sb = new StringBuilder();
    		StringBuilder sbWord = new StringBuilder();
    		char[] chars = src.trim().toCharArray();
    		for (int i = 0; i < chars.length; i++) {
    			char c = chars[i];
    			if(c >= 'A' && c <= 'Z') {
    				// 一旦遇到大写单词，保存之前已有字符组成的单词
    				if(sbWord.length() > 0) {
    					if(sb.length() > 0) {
    						sb.append("_");
    					}
    					sb.append(sbWord.toString());
    				}
    				sbWord = new StringBuilder();
    			}
    			sbWord.append(c);
    		}

    		if(sbWord.length() > 0) {
    			if(sb.length() > 0) {
    				sb.append("_");
    			}
    			sb.append(sbWord.toString());
    		}

    		return sb.toString();
    	}

    	public static boolean hasChinese(String s) {
    		String regexChinese = "[\u4e00-\u9fa5]+";
    		Pattern patternChinese = Pattern.compile(regexChinese);
    		return patternChinese.matcher(s).find();
    	}

    	public static boolean isEmpty(String s) {
    		return s == null || s.length() == 0;
    	}
    }

    
    static class FileUtils {

//    	public static void main(String[] args) {
//    		getCodeLinesDetail("E:\\work\\ChildrenRead\\app\\src\\main");
//    	}

    	/**
    	 * 递归获取的文件列表集合
    	 */
    	private static List<File> allFiles = new ArrayList<>();
    	
    	/**
    	 * 获取指定目录下全部文件
    	 * 
    	 * @param dir	根目录路径
    	 * @return			获取到的文件列表
    	 */
    	public static List<File> getAllFiles(String dir) {
    		return getAllFiles(new File(dir));
    	}

    	/**
    	 * 获取指定目录下全部文件
    	 * 
    	 * @param rootFile	根目录文件
    	 * @return			获取到的文件列表
    	 */
    	public static List<File> getAllFiles(File rootFile) {
    		allFiles = new ArrayList<File>();
    		try {
    			getFiles(rootFile);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return allFiles;
    	}
    	
    	/**
    	 * 递归dir下全部文件并保存至allFiles
    	 * 
    	 * @param dir		发起递归的根目录
    	 */
    	public static void getFiles(File dir) throws Exception {
    		File[] fs = dir.listFiles();
    		for (int i = 0; i < fs.length; i++) {
    			File file = fs[i];
    			if (fs[i].isDirectory()) {
    				try {
    					getFiles(fs[i]);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			} else {
    				allFiles.add(file);
    			}
    		}
    	}

    	/**
    	 * 使用文件通道的方式复制文件
    	 * 
    	 * @param srcFile
    	 *            源文件
    	 * @param tarFile
    	 *            复制到的新文件
    	 */
    	public static void copyFileByChannel(File srcFile, File tarFile) {
    		FileInputStream fi = null;
    		FileOutputStream fo = null;
    		FileChannel in = null;
    		FileChannel out = null;

    		try {
    			fi = new FileInputStream(srcFile);
    			fo = new FileOutputStream(tarFile);
    			in = fi.getChannel();// 得到对应的文件通道
    			out = fo.getChannel();// 得到对应的文件通道
    			// 连接两个通道，并且从in通道读取，然后写入out通道
    			in.transferTo(0, in.size(), out);
    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				fi.close();
    				in.close();
    				fo.close();
    				out.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    	/**
    	 * 替换指定目录下全部java文件内符合自定义条件的字符串
    	 * 
    	 * @param rootPath		根目录的绝对路径
    	 * @param replaceString	key-原文字 value-需要替换的文字
    	 */
    	public static void replaceStringOfJava(String rootPath, Map<String, String> replaceString) {
    		// 获取全部文件
    		List<File> files = FileUtils.getAllFiles(rootPath);
    		
    		for (File file : files) {
    			// 如果不是java后缀的文件,则跳过
    			if(!file.getName().endsWith(".java")) {
    				continue;
    			}
    			
    			// 将文件读取为一整个字符串
    			String fileContent = readToString(file);

    			// 是否有替换操作
    			boolean hasReplace = false;
    			// 遍历替换map,依次替换全部字符串
    			for (Map.Entry<String, String> entry : replaceString.entrySet()) {
    				if (fileContent.contains(entry.getKey())) {
    					fileContent = fileContent.replace(entry.getKey(), entry.getValue());
    					hasReplace = true;
    				}
    			}
    			
    			// 如果有替换操作,则将替换后的新文件内容字符串写入回文件中去
    			if(hasReplace) {
    				writeString2File(fileContent, file);
    			}
    		}
    	}
    	
    	/**
    	 * 替换指定目录下全部java文件内符合自定义条件的字符串,支持正则
    	 * 
    	 * @param rootPath		根目录的绝对路径
    	 * @param replaceString	key-原文字 value-需要替换的文字
    	 */
    	public static void replaceAllStringOfJava(String rootPath, Map<String, String> replaceString, String charSet) {
    		// 获取全部文件
    		List<File> files = FileUtils.getAllFiles(rootPath);
    		
    		for (File file : files) {
    			// 如果不是java后缀的文件,则跳过
    			if(!file.getName().endsWith(".java")) {
    				continue;
    			}
    			
    			// 将文件读取为一整个字符串
    			String fileContent = readToString(file, charSet);

    			// 是否有替换操作
    			boolean hasReplace = false;
    			// 遍历替换map,依次替换全部字符串
    			for (Map.Entry<String, String> entry : replaceString.entrySet()) {
    				if (fileContent.contains(entry.getKey())) {
    					fileContent = fileContent.replaceAll(entry.getKey(), entry.getValue());
    					hasReplace = true;
    				}
    			}
    			
    			// 如果有替换操作,则将替换后的新文件内容字符串写入回文件中去
    			if(hasReplace) {
    				writeString2File(fileContent, file, charSet);
    			}
    		}
    	}
    	
    	
    	/**
    	 * 删除无用java文件
    	 * 
    	 * @param rootPath		根目录的绝对路径
    	 */
    	public static void delNoUseJavaFile(String rootPath) {
    		List<File> files = getAllFiles(rootPath);
    		out:
    			for (File file : files) {
    				if(!file.getName().endsWith(".java")) {
    					continue;
    				}
    				
    				for (File compareFile : files) {
    					// 如果包含文件名,则视为有使用
    					String fileContent = readToString(compareFile);
    					if (fileContent.contains(getName(file))) {
    						continue out;
    					}
    				}
    				
    				String absname = file.getAbsoluteFile().getName();
    				boolean delete = file.delete();
    				System.out.println(absname + " ... delete=" + delete);
    			}
    	}
    	
    	/**
    	 * 获取代码行数详情,包括总/空行/注释/有效代码各个行数
    	 * 
    	 * @param rootPath	根目录的绝对路径
    	 */
    	public static void getCodeLinesDetail(String rootPath) {
    		// 全部文件中行数
    		int allLines = 0;
    		// 全部文件中空行数
    		int allEmptyLines = 0;
    		// 全部文件中代码行数
    		int allCodeLines = 0;
    		// 全部文件中注释行数
    		int allAnnoLines = 0;
    		
    		List<File> files = FileUtils.getAllFiles(rootPath);
    		for (File file : files) {
    			// TODO 只统计java和xml代码
    			if(file.getName().endsWith(".java") || file.getName().endsWith(".xml")) {
    				FileReader fr;
    				try {
    					fr = new FileReader(file);
    					BufferedReader bufferedreader = new BufferedReader(fr);
    					
    					String line;
    					// 是否属于多行注释
    					boolean multiLineAnno = false;
    					while ((line=bufferedreader.readLine()) != null) {
    						allLines ++;
    						
    						// 空行
    						if(line.trim().equals("")) {
    							allEmptyLines ++;
    							continue;
    						}
    						
    						// 单行注释
    						if(line.contains("//")) {
    							allAnnoLines ++;
    							continue;
    						}
    						
    						// 如果还是在多行注释中
    						if(multiLineAnno) {
    							allAnnoLines ++;
    							// 如果本行包含多行注释结束符,结束
    							if(line.contains("*/")) {
    								multiLineAnno = false;
    							}
    							continue;
    						}
    						
    						// 多行注释开始(包括/*和/**)
    						if(line.contains("/*")) {
    							allAnnoLines ++;
    							multiLineAnno = true;
    							continue;
    						}
    						
    						// 有效代码
    						allCodeLines ++;
    					}
    					fr.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    		System.out.println("文件总行数为：" + allLines);
    		System.out.println("文件空行数为：" + allEmptyLines);
    		System.out.println("文件注释行数为：" + allAnnoLines);
    		System.out.println("文件有效代码行数为：" + allCodeLines);
    		System.out.println("--------------------");
    		// TODO 计算比例规则为 注释行数/有效代码数
    		float percent = (float) allAnnoLines / allCodeLines * 100;
    		// 格式化百分比,保留2位小数  %50.00
    		DecimalFormat df = new DecimalFormat("0.00");
    		System.out.println("注释比例(注释行数/有效代码数): %" + df.format(percent));
    	}

    	/**
    	 * 获取代码行数,只统计java/xml后缀的文件
    	 * 
    	 * @param rootPath	根目录的绝对路径
    	 */
    	public static void getCodeLines(String rootPath) {
    		int allLines = 0;
    		List<File> files = getAllFiles(rootPath);
    		for (File file : files) {
    			if(file.getName().endsWith(".java") || file.getName().endsWith(".xml")) {
    				int lines = getLines(file);
    				allLines += lines;
    			}
    		}
    		System.out.println(allLines);
    	}

    	/**
    	 * 获取文件内文本的行数
    	 */
    	public static int getLines(File file) {
    		int lines = 0;
    		FileReader fr;
    		try {
    			fr = new FileReader(file);
    			BufferedReader bufferedreader = new BufferedReader(fr);
    			while ((bufferedreader.readLine()) != null) {
    				lines++;
    			}
    			fr.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		return lines;
    	}
    	
    	public static File getFileByName(String proPath, String filename) {
    		File tarFile = null;
    		
    		List<File> files = FileUtils.getAllFiles(proPath);
    		
    		for(File file : files) {
    			String fileName = file.getName();
    			if(fileName.equals(filename)) {
    				tarFile = file;
    				break;
    			}
    		}
    		
    		return tarFile;
    	}

    	/**
    	 * 获取文件名,去除后缀部分
    	 */
    	public static String getName(File file) {
    		String name = file.getName();
    		name = name.substring(0, name.lastIndexOf("."));
    		// 如果是.9.png结尾的,则在去除.png后缀之后还需要去除.9的后缀
    		if (file.getName().endsWith(".9.png")) {
    			name = name.substring(0, name.lastIndexOf("."));
    		}
    		return name;
    	}
    	
    	/**
    	 * 获取文件名,去除后缀部分
    	 */
    	public static String getName(String fileAbsPath) {
    		File file = new File(fileAbsPath);
    		return getName(file);
    	}
    	
    	/**
    	 * 文件名和后缀分开
    	 */
    	public static String[] getNameMap(File file) {
    		String[] nameMap = new String[2];
    		
    		String name = file.getName();
    		name = name.substring(0, name.lastIndexOf("."));
    		// 如果是.9.png结尾的,则在去除.png后缀之后还需要去除.9的后缀
    		if (file.getName().endsWith(".9.png")) {
    			name = name.substring(0, name.lastIndexOf("."));
    		}
    		
    		nameMap[0] = name;
    		nameMap[1] = file.getName().replaceFirst(name, "");
    		
    		return nameMap;
    	}

    	/**
    	 * 将文件读取为字符串
    	 */
    	public static String readToString(File file) {
    		Long filelength = file.length();
    		byte[] filecontent = new byte[filelength.intValue()];
    		try {
    			FileInputStream in = new FileInputStream(file);
    			in.read(filecontent);
    			in.close();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		try {
    			// 获取文件的编码格式,再根据编码格式生成字符串
    			String charSet = getCharSet(file);
    			return new String(filecontent, charSet);
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    			return null;
    		}
    	}

    	public static String parseCharset(String oldString, String oldCharset, String newCharset) {
    		byte[] bytes;
    		try {
    			bytes = oldString.getBytes(oldCharset);
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    			throw new RuntimeException("UnsupportedEncodingException - oldCharset is wrong");
    		}
    		try {
    			return new String(bytes, newCharset);
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    			throw new RuntimeException("UnsupportedEncodingException - newCharset is wrong");
    		}
    	}
    	
    	/**
    	 * 根据指定编码格式将文件读取为字符串
    	 */
    	public static String readToString(File file, String charSet) {
    		Long filelength = file.length();
    		byte[] filecontent = new byte[filelength.intValue()];
    		try {
    			FileInputStream in = new FileInputStream(file);
    			in.read(filecontent);
    			in.close();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		try {
    			return new String(filecontent, charSet);
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    			return null;
    		}
    	}
    	
    	/**
    	 * 将文件内容以行为单位读取
    	 */
    	public static ArrayList<String> readToStringLines(File file) {
    		ArrayList<String> strs = new ArrayList<String>();

    		FileReader fr = null;
    		BufferedReader br = null;
    		try {
    			fr = new FileReader(file);
    			br = new BufferedReader(fr);
    			String line;
    			while ((line = br.readLine()) != null) {
    				strs.add(line);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    			return null;
    		} finally {
    			if(br != null) {
    				try {
    					br.close();
    				} catch (IOException ignored) { }
    			}
    			if(fr != null) {
    				try {
    					fr.close();
    				} catch (IOException ignored) { }
    			}
    		}
    		
    		return strs;
    	}
    	
    	/**
    	 * 搜索某目录下所有文件的文本中,是否包含某个字段,如果包含打印改文件路径
    	 * 
    	 * @param path 搜索目录
    	 * @param key 包含字段
    	 */
    	public static void searchFileContent(String path, String key) {
    		List<File> allFiles = FileUtils.getAllFiles(path);
    		for(File file : allFiles) {
    			String string = FileUtils.readToString(file);
    			if(string.contains(key)) {
    				System.out.println(file.getAbsoluteFile());
    			}
    		}
    	}

    	/**
    	 * 获取文件编码格式,暂只判断gbk/utf-8
    	 */
    	public static String getCharSet(File file) {
    		String chatSet = null;
    		try {
    			InputStream in = new java.io.FileInputStream(file);
    			byte[] b = new byte[3];
    			in.read(b);
    			in.close();
    			if (b[0] == -17 && b[1] == -69 && b[2] == -65)
    				chatSet = "UTF-8";
    			else
    				chatSet = "GBK";
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		return chatSet;
    	}

    	/**
    	 * 将字符串写入文件
    	 */
    	public static void writeString2File(String str, File file, String encoding) {
            BufferedWriter writer = null;
            try {
            	if(!file.exists()) {
    				file.createNewFile();
            	}
    			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
    			writer.write(str);
    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				writer.close();
    			} catch (IOException e) {
    				writer = null;
    				e.printStackTrace();
    			}
    		}
    	}
    	
        public static void writeString2File(String str, File file) {  
        	writeString2File(str, file, getCharSet(file));
        } 
        
        /**
         * 将字节数组写入文件
         */
        public static void writeBytes2File(byte[] bytes, File file) {
        	FileOutputStream fos = null;
        	try {
        		if(!file.exists()) {
        			file.createNewFile();
        		}
        		
        		fos = new FileOutputStream(file);
        		fos.write(bytes);
        	} catch (IOException e) {
        		e.printStackTrace();
        	} finally {
        		try {
        			fos.close();
        		} catch (IOException e) {
        			fos = null;
        			e.printStackTrace();
        		}
        	}
        }

    }

}
