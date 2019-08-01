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
package zJdaUtilsLib.com.jagrosh.jdautilities.doc.standard;

import zJdaUtilsLib.com.jagrosh.jdautilities.doc.ConvertedBy;
import zJdaUtilsLib.com.jagrosh.jdautilities.doc.DocConverter;
import zJdaUtilsLib.com.jagrosh.jdautilities.doc.DocMultiple;

import java.lang.annotation.*;

@ConvertedBy(Error.Converter.class)
@DocMultiple(
    preface = "**Possible Errors:**\n\n",
    prefixEach = "+ ",
    separateBy = "\n\n")
@Documented
@Repeatable(Errors.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Error {
    String value();
    String response() default "";
    String prefix() default "";
    class Converter implements DocConverter<Error> {
        @Override
        public String read(Error annotation) {
            StringBuilder b = new StringBuilder(annotation.prefix());
            if(!annotation.response().isEmpty()) b.append("\"").append(annotation.response()).append("\" - ");
            b.append(annotation.value());
            return b.toString();
        }
    }
}
