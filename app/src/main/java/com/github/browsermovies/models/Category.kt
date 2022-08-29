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
data class Category(
    var id: Long ,
    var name: String,
    var description: String,
    var link: String,
    var title: String,
    var next: String,
    var localImageResource: String


) : Serializable {

    override fun toString(): String {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", next='" + next + '\'' +
                ", link='" + link + '\'' +
                ", localImageResource='" + localImageResource + '\'' +
                '}'
    }

    fun getLocalImageResourceName(): String? {
        return localImageResource
    }

    companion object {
        internal const val serialVersionUID = 7275661343456454
    }
}
