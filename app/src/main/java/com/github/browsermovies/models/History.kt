/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.browsermovies.models

import java.io.Serializable

/**
 * Movie class represents video entity with title, description, image thumbs and video url.
 */


data class History(
    var id: Int = 0,
    var title: String? = null,
    var category: String? = null,
    var playlink: String? = null,
    var playindex:Int = 0,
    var playMovie:Movie? = null,
    var playSite:Site? = null,
    var playtime:Long = 0
) : Serializable {

    override fun toString(): String {
        return "History{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", playlink='" + playlink + '\'' +
                ", index='" + playindex + '\'' +
                ", playMovie='" + playMovie.toString() + '\'' +
                ", playSite='" + playSite.toString() + '\'' +
                ", playtime ="+playtime
                '}'
    }

    companion object {
        internal const val serialVersionUID = 7275663234354543L
    }
}
