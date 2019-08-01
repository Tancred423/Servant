/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zJdaUtilsLib.com.jagrosh.jdautilities.doc;

import zJdaUtilsLib.com.jagrosh.jdautilities.commons.utils.FixedSizeCache;
import zJdaUtilsLib.com.jagrosh.jdautilities.doc.standard.CommandInfo;
import zJdaUtilsLib.com.jagrosh.jdautilities.doc.standard.Error;
import zJdaUtilsLib.com.jagrosh.jdautilities.doc.standard.RequiredPermissions;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DocGenerator {
    private final HashMap<Class<? extends Annotation>, DocConverter<? extends Annotation>> map;
    private final FixedSizeCache<AnnotatedElement, String> cache;
    private final String separator;

    public static DocGenerator getDefaultGenerator() {
        return new DocGenerator()
                .register(CommandInfo.class)
                .register(Error.class)
                .register(RequiredPermissions.class);
    }

    public DocGenerator() {
        this(20);
    }
    public DocGenerator(int cacheSize)
    {
        this("\n\n", cacheSize);
    }
    public DocGenerator(String separator, int cacheSize) {
        this.separator = separator;
        map = new HashMap<>();
        cache = new FixedSizeCache<>(cacheSize);
    }

    public String getDocFor(Class<?> cla)
    {
        return read(cla);
    }
    public String getDocFor(Method method)
    {
        return read(method);
    }

    public List<String> getDocForMethods(Class<?> cla) {
        List<String> list = new ArrayList<>();
        for(Method method : cla.getMethods()) {
            String doc = read(method);
            if(!doc.isEmpty()) list.add(doc);
        }
        return list;
    }

    @SuppressWarnings({"JavaReflectionMemberAccess","unchecked"})
    public <T extends Annotation> DocGenerator register(Class<T> type, Object... converterParams) {
        ConvertedBy convertedBy = type.getAnnotation(ConvertedBy.class);

        if(convertedBy == null) throw new IllegalArgumentException("Illegal annotation type! Not annotated with @ConvertedBy!");

        final DocConverter<T> instance;
        try {
            // If parameters are specified
            if(converterParams.length > 0) {
                Class<?>[] tArray = Arrays.stream(converterParams)
                        .map(Object::getClass)
                        .collect(Collectors.toList())
                        .toArray(new Class[converterParams.length]);
                instance = convertedBy.value().getDeclaredConstructor(tArray).newInstance(converterParams);
            } else {
                instance = convertedBy.value().getConstructor().newInstance();
            }
        } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException("Instance of "+convertedBy.value()+" could not be instantiated!", e);
        }

        return register(type, instance);
    }


    public <T extends Annotation> DocGenerator register(Class<T> type, DocConverter<T> converter) {
        synchronized(map) { map.put(type, converter);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private String read(AnnotatedElement ae) {
        // Have we already read this?
        if(cache.contains(ae))
            return cache.get(ae);

        StringBuilder b = new StringBuilder();
        synchronized(map) {
            int lastIndex = map.keySet().size() - 1;
            int index = 0;
            for(Class<? extends Annotation> key : map.keySet()) {
                DocMultiple docMultiple = key.getAnnotation(DocMultiple.class);
                if(docMultiple == null) {
                    Annotation a = ae.getAnnotation(key);

                    // Is not annotated with that particular annotation
                    if(a == null) continue;

                    b.append(((DocConverter<Annotation>)map.get(key)).read(a));
                    if(index < lastIndex) b.append(separator);
                } else {
                    Annotation[] ans = ae.getAnnotationsByType(key);
                    int len = ans.length;
                    for(int i = 0; i < len; i++) {
                        if(i == 0) b.append(docMultiple.preface());

                        b.append(docMultiple.prefixEach()).append(((DocConverter<Annotation>)map.get(key)).read(ans[i]));

                        if(i < len - 1) b.append(docMultiple.separateBy());
                        else if(index < lastIndex) b.append(separator);
                    }
                }
            }
        }

        // Trim this down
        String doc = b.toString().trim();

        // Cache the read value, even if it's empty.
        cache.add(ae, doc);

        return doc;
    }
}
