package home.system.chromecast;

import home.controller.PS;
import home.parcel.Parcel;
import home.parcel.SystemException;


/**
 * NPR Hour update module for audio
 *
 * Created by Will on 3/1/2017.
 */


class RSS {

    static Parcel getNPRRss(String rssURL) throws SystemException{
        Parcel info = Parcel.PROCESS_XML_URL(rssURL);
        Parcel p = new Parcel();
        p.put("app", "media-player");
        p.put("url",info.getParcel("channel").getParcel("item").getParcel("enclosure").getString("url"));
        p.put("image",info.getParcel("channel").getParcel("image").getString("url") );
        return p;
    }


    static Parcel getHourMeidaInfo() throws SystemException {
       return getNPRRss("https://www.npr.org/rss/podcast.php?id=500005");

    }

    static Parcel getBusinessStory() throws SystemException{
        return getNPRRss("https://www.npr.org/templates/rss/podlayer.php?id=1095");
    }

    static Parcel getMorningEdition() throws SystemException{
        return getNPRRss("https://www.npr.org/rss/podcast.php?id=510318");
    }


}


/**
 * Class used to make radiostations
 */
class radioStation{

    static Parcel radioStations() throws SystemException {
        Parcel p = new Parcel();
        p.put(PS.ChromeCastPS.RADIO_70S , makeRadioUrl("70's Hits","http://rfcmedia.streamguys1.com/70hits.mp3"));
        p.put(PS.ChromeCastPS.RADIO_80S , makeRadioUrl("80's Hits","http://rfcmedia.streamguys1.com/80hits.mp3"));
        p.put(PS.ChromeCastPS.RADIO_90S , makeRadioUrl("90's Hits","http://rfcmedia.streamguys1.com/90hits.mp3"));
        p.put(PS.ChromeCastPS.RADIO_CLASSIC_ROCK , makeRadioUrl("Classic Hits","http://rfcmedia.streamguys1.com/classicoldies.mp3"));
        p.put(PS.ChromeCastPS.RADIO_CLASSIC_ROCK_1 , makeRadioUrl("Classic Hits","http://rfcmedia.streamguys1.com/classicrock.mp3"));
        p.put(PS.ChromeCastPS.RADIO_CLASSICAL , makeRadioUrl("Sounds of the Symphony","http://rfcmedia.streamguys1.com/classical.mp3"));
        p.put(PS.ChromeCastPS.RADIO_WITR , makeRadioUrl("WITR","http://streaming.witr.rit.edu:8000/witr-mp3-80"));
        p.put(PS.ChromeCastPS.RADIO_ROCK , makeRadioUrl("Third Rock Radio","http://rfcmedia2.streamguys1.com/thirdrock.mp3"));
        p.put(PS.ChromeCastPS.RADIO_NEWS , makeRadioUrl("NPR Hourly update", RSS.getHourMeidaInfo()));
        p.put(PS.ChromeCastPS.RADIO_BUSINESS , makeRadioUrl("NPR Business Stories", RSS.getBusinessStory()));
        p.put(PS.ChromeCastPS.RADIO_MORNING_NEWS, makeRadioUrl("NPR Morning Edition", RSS.getMorningEdition()));
        return p;
    }


    static private Parcel makeRadioUrl(String title, String url){
        Parcel p = new Parcel();
        p.put("title", title);
        p.put("url", url);
        p.put("app","meida-player");
        return p;
    }

    static Parcel makeRadioUrl(String title, Parcel info) throws SystemException {
        Parcel p = new Parcel();
        p.put("title", title);
        p.put("url", info.getString("url"));
        p.put("app","meida-player");
        p.put("image", info.getString("image"));
        return p;

    }

}
