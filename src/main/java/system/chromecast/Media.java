package system.chromecast;

import controller.Logger;
import parcel.Parcel;
import parcel.SystemException;


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


}

class ThirdRockRadio{
    private static String radioURL = "http://rfcmedia2.streamguys1.com/thirdrock.mp3";
    private static String imageURL = "http://cdn-radiotime-logos.tunein.com/s151799q.png";

    static Parcel getMeidaInfo(){
        Parcel p = new Parcel();
        p.put("app", "media-player");
        p.put("source", radioURL);
        p.put("image", imageURL);
        return p;

    }
}

class YouTube{
    //https://developers.google.com/api-client-library/java/

    private static String youtubeBaseURL = " ";
    static Parcel getMeidaInfo(){
        Parcel p = new Parcel();

        return p;
    }

    static Parcel getFirstSearchresult(String search)
    {
        Parcel p = new Parcel();

        return p;
    }

}

class   ClassicalMP3{
    /*
    Play Random classical song stored somewhere on the cloud?
    google classical music
     */
}


class radioStation{

    static Parcel radioStations() throws SystemException {
        Parcel p = new Parcel();
        p.put("70s", makeRadioUrl("70's Hits","http://rfcmedia.streamguys1.com/70hits.mp3"));
        p.put("80s", makeRadioUrl("80's Hits","http://rfcmedia.streamguys1.com/80hits.mp3"));
        p.put("90s", makeRadioUrl("90's Hits","http://rfcmedia.streamguys1.com/90hits.mp3"));
        p.put("classicRock", makeRadioUrl("Classic Hits","http://rfcmedia.streamguys1.com/classicoldies.mp3"));
        p.put("classicRock1", makeRadioUrl("Classic Hits","http://rfcmedia.streamguys1.com/classicrock.mp3"));
        p.put("classical", makeRadioUrl("Sounds of the Symphony","http://rfcmedia.streamguys1.com/classical.mp3"));
        p.put("witr", makeRadioUrl("WITR","http://streaming.witr.rit.edu:8000/witr-mp3-80"));
        p.put("rock", makeRadioUrl("Third Rock Radio","http://rfcmedia2.streamguys1.com/thirdrock.mp3"));
        p.put("news", makeRadioUrl("NPR Hourly update", RSS.getHourMeidaInfo()));
        p.put("business", makeRadioUrl("NPR Business Stories", RSS.getBusinessStory()));

        return p;
    }


    static Parcel makeRadioUrl(String title, String url){
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
