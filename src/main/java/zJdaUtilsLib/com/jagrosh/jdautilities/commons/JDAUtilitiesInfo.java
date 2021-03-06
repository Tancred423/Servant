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
package zJdaUtilsLib.com.jagrosh.jdautilities.commons;

public final class JDAUtilitiesInfo {
    public static final String VERSION_MAJOR;
    public static final String VERSION_MINOR;
    public static final String VERSION;
    public static final String GITHUB = "https://github.com/JDA-Applications/JDA-Utilities";
    public static final String AUTHOR = "JDA-Applications";

    // Version init block
    static {
        Package pkg = JDAUtilitiesInfo.class.getPackage();

        String version = pkg.getImplementationVersion();
        VERSION = version == null? "DEV" : version;

        String[] parts = VERSION.split("\\.", 2);
        VERSION_MAJOR = version == null? "2" : parts[0]; // This should only be updated every version major!
        VERSION_MINOR = version == null? "X" : parts[1];
    }
}
