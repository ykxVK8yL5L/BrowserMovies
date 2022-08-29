package com.github.browsermovies.models
import org.json.JSONObject
import java.io.Serializable



data class Site(
    var id: Long,
    var name: String,
    var description: String,
    var link: String,
    var title: String,
    var openBrowser:String,
    var localImageResource: String,
    var presenter: String,
    var inword: String,
    var uninword: String,
    var next: String,
    var thumb: String,
    val category: List<Category>,
    var list:ListRule,
    var detail:DetailRule,
    var play:PlayRule,
    var search:SearchRule

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
    var useragent: String,
    var fetchtype: String,
    var pagefilter:String,
    var pagerule: String,
    var pageone: String,
    var videoscontainer: SelectorWithAtrr,
    var thumb: SelectorWithAtrr,
    var link: SelectorWithAtrr,
    var title:SelectorWithAtrr,
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
    var useragent: String,
    var fetchtype: String,
    var videoscontainer: SelectorWithAtrr,
    var clickcontainer: SelectorWithAtrr,
    var link: SelectorWithAtrr,
    var title:SelectorWithAtrr,
    var onclick:String,
    var play:String
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
    var useragent: String,
    var type: String,
    var filter: String,
    var player: String
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
    var useragent: String,
    var fetchtype: String,
    var pagerule: String,
    var pageone: String,
    var videoscontainer: SelectorWithAtrr,
    var thumb: SelectorWithAtrr,
    var link: SelectorWithAtrr,
    var title:SelectorWithAtrr,
    var pagesize:Int
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
    var selector: String,
    var attrName: String,
    var filter:String
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



