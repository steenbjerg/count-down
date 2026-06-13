package dk.stonemountain.count.down.util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class JsonbHelper {
  private static final Jsonb JSONB = JsonbBuilder.create();

  public static <T> T fromJson(String json, Class<T> clazz) {
    return JSONB.fromJson(json, clazz);
  }

  public static <T> List<T> fromJson(String json, ParameterizedType type) {
    return JSONB.fromJson(json, type);
  }

  public static String toJson(Object object) {
    return JSONB.toJson(object);
  }
}
