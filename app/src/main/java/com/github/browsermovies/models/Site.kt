package com.github.browsermovies.models
import org.json.JSONObject
import java.io.Serializable



data class Site(
    var id: Long = 0,
    var name: String? = "",
    var description: String? = "",
    var link: String? = "",
    var title: String? = "",
    var openBrowser:String = "no",
    var localImageResource: String? = "",
    var presenter: String? = "Movie",
    var inword: String? = "m3u8,mp4",
    var uninword: String? = "",
    var next: String? = "detail",
    var thumb: String? = "",
    val category: List<Category>? = null,
    var list:ListRule? = null,
    var detail:DetailRule? = null,
    var play:PlayRule? = null,
    var search:SearchRule? = null

) : Serializable {

    override fun toString(): String {
        return "Site{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", openBrowser='" + openBrowser + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", localImageResource='" + localImageResource + '\'' +
                ", presenter='" + presenter + '\'' +
                ", uninword='" + uninword + '\'' +
                ", next='" + next + '\'' +
                ", thumb='" + thumb + '\'' +
                ", category='" + category.toString() + '\'' +
                ", list='" + list.toString() + '\'' +
                ", detail='" + detail.toString() + '\'' +
                ", search='" + search.toString() + '\'' +
                '}'
    }

    fun getLocalImageResourceName(): String? {
        return localImageResource
    }

    companion object {
        internal const val serialVersionUID = 7275661343455454
    }
}



data class ListRule(
    var useragent: String? = "default",
    var fetchtype: String? = "default",
    var pagefilter:String? = "",
    var pagerule: String? = "",
    var pageone: String? = "",
    var videoscontainer: SelectorWithAtrr? =null,
    var thumb: SelectorWithAtrr? = null,
    var link: SelectorWithAtrr? = null,
    var title:SelectorWithAtrr?=null,
    var pagesize:Int = 0
): Serializable {
    override fun toString(): String {
        return "ListRule{" +
                "useragent='" + useragent + '\'' +
                "pagefilter=" + pagefilter +'\'' +
                "pagerule=" + pagerule +'\'' +
                ", pageone='" + pageone + '\'' +
                ", videoscontainer='" + videoscontainer.toString() + '\'' +
                ", thumb='" + thumb.toString() + '\'' +
                ", link='" + link.toString() + '\''
                '}'
    }
    companion object {
        internal const val serialVersionUID = 727566134345465676
    }
}

data class DetailRule(
    var useragent: String? = "default",
    var fetchtype: String? = "default",
    var videoscontainer: SelectorWithAtrr? = null,
    var clickcontainer: SelectorWithAtrr? = null,
    var link: SelectorWithAtrr? = null,
    var title:SelectorWithAtrr?=null,
    var onclick:String="no",
    var play:String = "no"
): Serializable {
    override fun toString(): String {
        return "DetailRule{" +
                "useragent='" + useragent + '\'' +
                ", videoscontainer='" + videoscontainer.toString() + '\'' +
                ", link='" + link.toString() + '\'' +
                ", onclick='" + onclick + '\'' +
                ", clickcontainer='" + clickcontainer.toString() + '\'' +
                ", play='" + play + '\'' +
                ", title='" + title.toString()
        '}'
    }
    companion object {
        internal const val serialVersionUID = 727566134345465677
    }
}

data class PlayRule(
    var useragent: String? = "default",
    var type: String? = "default",
    var filter: String? = "",
    var player: String? = "ijk"
): Serializable {
    override fun toString(): String {
        return "PlayRule{" +
                "type='" + type + '\'' +
                "useragent='" + useragent + '\'' +
                "player='" + player + '\'' +
        '}'
    }
    companion object {
        internal const val serialVersionUID = 727566134345465678
    }
}



data class SearchRule(
    var useragent: String? = "default",
    var fetchtype: String? = "default",
    var pagerule: String? = "",
    var pageone: String? = "",
    var videoscontainer: SelectorWithAtrr? = null,
    var thumb: SelectorWithAtrr? = null,
    var link: SelectorWithAtrr? = null,
    var title:SelectorWithAtrr?=null,
    var pagesize:Int = 0
): Serializable {
    override fun toString(): String {
        return "SearchRule{" +
                "pagerule=" + pagerule +
                "useragent='" + useragent + '\'' +
                ", pageone='" + pageone + '\'' +
                ", videoscontainer='" + videoscontainer.toString() + '\'' +
                ", thumb='" + thumb.toString() + '\'' +
                ", pagesize='" + pagesize.toString() + '\'' +
                ", link='" + link.toString()
        '}'
    }
    companion object {
        internal const val serialVersionUID = 727566123445465679
    }
}




data class SelectorWithAtrr(
    var selector: String? = "",
    var attrName: String? = "",
    var filter:String? = ""
): Serializable {
    override fun toString(): String {
        return "SelectorWithAtrr{" +
                ", selector='" + selector + '\'' +
                ", attrName='" + attrName + '\'' +
                ", filter='" + filter + '\'' +
        '}'
    }
    companion object {
        internal const val serialVersionUID = 7275661343466811
    }
}


