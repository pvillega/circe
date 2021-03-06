package io.circe.jackson

import cats.data.Xor
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module.SetupContext
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.{
  BeanDescription,
  DeserializationConfig,
  JavaType,
  JsonDeserializer,
  JsonSerializer,
  SerializationConfig
}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers
import io.circe.Json

object CirceJsonModule extends SimpleModule("CirceJson", Version.unknownVersion()) {
  override def setupModule(context: SetupContext): Unit = {
    context.addDeserializers(
      new Deserializers.Base {
        override def findBeanDeserializer(
          javaType: JavaType,
          config: DeserializationConfig,
          beanDesc: BeanDescription
        ) = {
          val klass = javaType.getRawClass
          if (classOf[Json].isAssignableFrom(klass) || klass == Json.JNull.getClass) {
            new CirceJsonDeserializer(config.getTypeFactory, klass)
          } else null
        }
      }
    )

    context.addSerializers(
      new Serializers.Base {
        override def findSerializer(
          config: SerializationConfig,
          javaType: JavaType,
          beanDesc: BeanDescription
        ) = {
          val ser: Object = if (classOf[Json].isAssignableFrom(beanDesc.getBeanClass)) {
            CirceJsonSerializer
          } else null

          ser.asInstanceOf[JsonSerializer[Object]]
        }
      }
    )
  }
}
