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

import java.lang.annotation.*;

@ConvertedBy(CommandInfo.Converter.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandInfo {
    String[] name() default {};
    String usage() default "";
    String description() default "";
    String[] requirements() default {};
    class Converter implements DocConverter<CommandInfo> {
        @Override
        public String read(CommandInfo annotation) {
            String[] names = annotation.name();
            String usage = annotation.usage();
            String description = annotation.description();
            String[] requirements = annotation.requirements();

            StringBuilder b = new StringBuilder();

            if(names.length > 0) {
                b.append("**Name:** `").append(names[0]).append("`").append("\n\n");
                if(names.length > 1) {
                    b.append("**Aliases:**");
                    for(int i = 1; i < names.length; i++)
                        b.append(" `").append(names[i]).append("`").append(i != names.length - 1 ? "," : "\n\n");
                }
            }

            if(!usage.isEmpty()) b.append("**Usage:** ").append(usage).append("\n\n");
            if(!description.isEmpty()) b.append("**Description:** ").append(description).append("\n\n");
            if(requirements.length == 1) b.append("**Requirement:** ").append(requirements[0]).append("\n\n");
            else if(requirements.length > 1) {
                b.append("**Requirements:**\n");
                for(int i = 1; i <= requirements.length; i++) {
                    b.append(i).append(") ").append(requirements[i - 1]);
                    if(i != requirements.length)
                        b.append("\n");
                }
            }

            return b.toString();
        }
    }
}
