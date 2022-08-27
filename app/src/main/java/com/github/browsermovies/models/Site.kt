package com.github.browsermovies.models
import org.json.JSONObject
import java.io.Serializable



data class Site(
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var link: String? = null,
    var title: String? = null,
    var openBrowser:String = "no",
    var localImageResource: String? = null,
    var presenter: String? = "Movie",
    var inword: String? = "m3u8,mp4",
    var uninword: String? = null,
    var next: String? = "detail",
    var thumb: String? = null,
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
                ", linkurl='" + link + '\'' +
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
    var pagerule: String? = null,
    var pageone: String? = null,
    var videoscontainer: SelectorWithAtrr? = null,
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
        internal const val serialVersionUID = 727566134345465676
    }
}

data class PlayRule(
    var useragent: String? = "default",
    var filter: String? = "",
    var player: String? = "ijk"
): Serializable {
    override fun toString(): String {
        return "DetailRule{" +
                "useragent='" + useragent + '\'' +
                "player='" + player + '\'' +
        '}'
    }
    companion object {
        internal const val serialVersionUID = 727566134345465676
    }
}



data class SearchRule(
    var useragent: String? = "default",
    var fetchtype: String? = "default",
    var pagerule: String? = null,
    var pageone: String? = null,
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
        internal const val serialVersionUID = 727566123445465676
    }
}




data class SelectorWithAtrr(
    var selector: String? = null,
    var attrName: String? = null,
    var filter:String? = null
): Serializable {
    override fun toString(): String {
        return "SelectorWithAtrr{" +
                ", selector='" + selector + '\'' +
                ", attrName='" + attrName + '\'' +
                ", filter='" + filter + '\'' +
        '}'
    }
    companion object {
        internal const val serialVersionUID = 7275661343466644
    }
}



