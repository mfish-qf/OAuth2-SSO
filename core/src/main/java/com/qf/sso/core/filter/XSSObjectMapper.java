package com.qf.sso.core.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.qf.sso.core.common.JSoupUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author qiufeng
 * @date 2020/2/10 19:16
 */
public class XSSObjectMapper extends ObjectMapper {
    public XSSObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new JsonHtmlXssSerializer());
        this.registerModule(module);
        this.setTimeZone(TimeZone.getDefault());
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    class JsonHtmlXssSerializer extends JsonSerializer<String> {

        public JsonHtmlXssSerializer() {
            super();
        }

        public Class<String> handledType() {
            return String.class;
        }

        public void serialize(String value, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            if (value != null) {
                String result = JSoupUtil.clean(value);
                jsonGenerator.writeString(result);
            }
        }
    }
}
