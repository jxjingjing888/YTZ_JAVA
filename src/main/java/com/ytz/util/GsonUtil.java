package com.ytz.util;



import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于json字符串跟对象的转换
 *
 * @author 吴宇春
 */
@Slf4j
public class GsonUtil {

    private static final Gson PARSER = new Gson();

    private static final Gson JAXB_PARSER = new GsonBuilder()
            .registerTypeAdapter(XMLGregorianCalendar.class, new XGCalConverter.Serializer())
            .registerTypeAdapter(XMLGregorianCalendar.class, new XGCalConverter.Deserializer()).serializeNulls()
            .enableComplexMapKeySerialization().create();

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUtil.class);

    /**
     * 转换为json字符串(jaxb 即wsdl)
     *
     * @param obj 待序列化的对象
     * @return 序列化后的json字符串
     */
    public static String toJaxbJson(Object obj) {
        return JAXB_PARSER.toJson(obj);
    }

    /**
     * 将json字符串转换为对象(jaxb 即wsdl)
     *
     * @param json   json字符串
     * @param target 目标类型的类对象
     * @param <T>    目标类型
     * @return 转换后的实例
     */
    public static <T> T toJaxbObject(String json, Class<T> target) {
        return JAXB_PARSER.fromJson(json, target);
    }

    /**
     * 转换为json字符串
     *
     * @param obj 待序列化的对象
     * @return 序列化后的json字符串
     */
    public static String toJson(Object obj) {
        return PARSER.toJson(obj);
    }

    /**
     * 将对象转换成Gson中对应的json对象
     *
     * @param obj 待转换的对象
     * @return 返回转换后的实例
     */
    public static JsonElement toJsonTree(Object obj) {
        return PARSER.toJsonTree(obj);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json   json字符串
     * @param target 目标类型的类对象
     * @param <T>    目标类型
     * @return 转换后的实例
     */
    public static <T> T toObject(String json, Class<T> target) {
        return PARSER.fromJson(json, target);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json   json字符串
     * @param target 目标类型的类对象
     * @param <T>    目标类型
     * @return 转换后的实例
     */
    public static <T> T toObject(JsonElement json, Class<T> target) {
        return PARSER.fromJson(json, target);
    }

    public static <T> List<T> toList(String arrayJson, Class<T> itemClazz) {
        return toList((JsonArray) PARSER.fromJson(arrayJson, JsonElement.class), itemClazz);
    }

    public static <T> List<T> toList(JsonArray array, Class<T> itemClazz) {
        if (isEmpty(array)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (JsonElement item : array) {
            result.add(PARSER.fromJson(item, itemClazz));
        }
        return result;
    }

    /**
     * 判断json是否为 {@link JsonArray#size()} == 0, {@link JsonNull}
     *
     * @param element
     * @return
     */
    public static boolean isEmpty(JsonElement element) {
        if (null == element) {
            return true;
        }
        if (element.isJsonNull()) {
            return true;
        } else if (element instanceof JsonArray && ((JsonArray) element).size() == 0) {
            return true;
        }
        return element instanceof JsonObject && ((JsonObject) element).entrySet().isEmpty();
    }

    /**
     * 从节点中安全获取对应的数字。如果element为null，或者不为JsonPrimitive实例，或者不为数字类型，都会返回默认值。
     *
     * @param element    待获取的节点
     * @param defaultVal 默认值，如果获取失败，将返回该值。
     * @return 获取后的值
     */
    public static int safeGetNumber(JsonElement element, int defaultVal) {
        if (null == element || !element.isJsonPrimitive()) {
            LOGGER.debug("参数异常:{};将会返回默认值：{}", element, defaultVal);
            return defaultVal;
        }

        if (!((JsonPrimitive) element).isNumber()) {
            LOGGER.debug("参数不为数字类型。实例为：{}", element);
            return defaultVal;
        }
        return element.getAsInt();
    }

    /**
     * 检查json类型，并且返回对应的类型。
     *
     * @param element     待检查的实例
     * @param targetClass 目标类型的类实例
     * @param wrongMsg    如果不为目标信息，将抛出对应的"IllegalArgumentException"。wrongMsg则为该except的信息
     * @param <T>         目标类型
     * @return 转换后的类型实例
     */
    public static <T extends JsonElement> T checkJsonEleType(JsonElement element, Class<T> targetClass,
                                                             String wrongMsg) {
        if (element == null || !targetClass.isAssignableFrom(element.getClass())) {
            throw new IllegalArgumentException(wrongMsg);
        }
        return (T) element;
    }

    public static boolean isBlank(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isJsonNull()) {
            return true;
        }
        return false;
    }

    public static boolean isBlank(JsonArray jsonArray) {
        if (jsonArray == null || jsonArray.isJsonNull() || jsonArray.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotBlank(JsonObject jsonObject) {
        return !isBlank(jsonObject);
    }

    public static boolean isNotBlank(JsonArray jsonArray) {
        return !isBlank(jsonArray);
    }

    /**
     * 将数据转换为对应的数据类型，必须保证数据结构跟目标类型一致
     *
     * @param data        待转换的数据
     * @param targetClazz 目标类型的类对象
     * @param <T>         目标类型
     * @return 转换后的结果
     */
    public static <T> T typeConvert(Object data, Class<T> targetClazz) {
        JsonElement json = toJsonTree(data);
        return toObject(json, targetClazz);
    }

    /**
     * 深度拷贝数据
     *
     * @param source 原数据
     * @param <T>    类型
     * @return 返回深度copy的数据
     */
    public static <T> T deepClone(T source) {
        JsonElement jsonEle = toJsonTree(source);
        return (T) toObject(jsonEle, source.getClass());
    }

    /**
     * JsonElement 转int
     *
     * @param jsonElement
     * @return
     */
    public static int toInt(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return 0;
        }
        return jsonElement.getAsInt();
    }

    /**
     * JsonElement 转long
     *
     * @param jsonElement
     * @return
     */
    public static long toLong(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return 0;
        }
        return jsonElement.getAsLong();
    }

    /**
     * JsonElement 转Double
     *
     * @param jsonElement
     * @return
     */
    public static Double toDouble(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return 0D;
        }
        return jsonElement.getAsDouble();
    }

    /**
     * JsonElement 转String
     *
     * @param jsonElement
     * @return
     */
    public static String toString(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return "";
        }
        return jsonElement.getAsString();
    }

    /**
     * JsonElement BigDecimal
     *
     * @param jsonElement
     * @return
     */
    public static BigDecimal toBigDecimal(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return BigDecimal.ZERO;
        }
        return jsonElement.getAsBigDecimal();
    }

    /**
     * JsonElement boolean
     *
     * @param jsonElement
     * @return
     */
    public static boolean toBoolean(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return false;
        }
        return jsonElement.getAsBoolean();
    }

    /**
     * JsonElement 强转 JsonArray
     *
     * @param jsonElement
     * @return
     */
    public static JsonArray toJsonArray(JsonElement jsonElement) {
        if (isEmpty(jsonElement)) {
            return new JsonArray();
        }
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }
        JsonArray analysisArray = new JsonArray();
        analysisArray.add(jsonElement);
        return analysisArray;
    }
}
