package com.events.events.models.serializers;

import com.events.events.services.AWSS3Service;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CustomURLSerializer extends StdSerializer<String> {

    @Autowired
    private AWSS3Service awss3Service;

    public CustomURLSerializer(){
        this(null);
    }

    public CustomURLSerializer(Class<String> t){
        super(t);
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(s == null){
            jsonGenerator.writeString("null");
        }else{
            jsonGenerator.writeString(awss3Service.getPreSignedUrl(s).toString());
        }
    }

}
