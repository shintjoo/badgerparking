package com.cs407.badgerparking;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

//this code is sourced from:
//https://stackoverflow.com/questions/6093975/how-to-build-a-rss-reader-for-android

public class RssParser extends DefaultHandler {

    private StringBuilder   content;
    private boolean         inChannel;
    private boolean         inImage;
    private boolean         inItem;

    private final ArrayList<Item> items   = new ArrayList<>();
    private final Channel         channel = new Channel();

    private Item            lastItem;


    public RssParser(String url) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            URL sourceUrl = new URL(url);
            xr.setContentHandler(this);
            xr.parse(new InputSource(sourceUrl.openStream()));
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }


    public class Item {

        private String title;
        private String description;
        private String link;
        private String category;
        private String pubDate;
        private String guid;

        public String getTitle(){
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getLink() {
            return link;
        }

        public String getCategory() {
            return category;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getGuid() {
            return guid;
        }
    }

    public class Channel {
        public String title;
        public String description;
        public String link;
        public String lastBuildDate;
        public String generator;
        public String imageUrl;
        public String imageTitle;
        public String imageLink;
        public String imageWidth;
        private String imageHeight;
        public String imageDescription;
        public String language;
        public String copyright;
        public String pubDate;
        public String category;
        public String ttl;
    }


    @Override
    public void startDocument() throws SAXException {
        Log.i("LOG", "StartDocument");
    }


    @Override
    public void endDocument() throws SAXException {
        Log.i("LOG", "EndDocument");
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equalsIgnoreCase("image")) {
            inImage = true;
        }

        if (localName.equalsIgnoreCase("channel")) {
            inChannel = true;
        }

        if (localName.equalsIgnoreCase("item")) {
            lastItem = new Item();
            items.add(lastItem);
            inItem = true;
        }

        content = new StringBuilder();
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equalsIgnoreCase("image")) {
            inImage = false;
        }

        if (localName.equalsIgnoreCase("channel")) {
            inChannel = false;
        }

        if (localName.equalsIgnoreCase("item")) {
            inItem = false;
        }

        if (localName.equalsIgnoreCase("title")) {
            if (content == null) {
                return;
            }

            if (inItem) {
                lastItem.title = content.toString();
            } else if (inImage) {
                channel.imageTitle = content.toString();
            } else if (inChannel) {
                channel.title = content.toString();
            }

            content = null;
        }

        if (localName.equalsIgnoreCase("description")) {
            if (content == null) {
                return;
            }

            if (inItem) {
                lastItem.description = content.toString();
            } else if (inImage) {
                channel.imageDescription = content.toString();
            } else if (inChannel) {
                channel.description = content.toString();
            }

            content = null;
        }

        if (localName.equalsIgnoreCase("link")) {
            if (content == null) {
                return;
            }

            if (inItem) {
                lastItem.link = content.toString();
            } else if (inImage) {
                channel.imageLink = content.toString();
            } else if (inChannel) {
                channel.link = content.toString();
            }

            content = null;
        }

        if (localName.equalsIgnoreCase("category")) {
            if (content == null) {
                return;
            }

            if (inItem) {
                lastItem.category = content.toString();
            } else if (inChannel) {
                channel.category = content.toString();
            }

            content = null;
        }

        if (localName.equalsIgnoreCase("pubDate")) {
            if (content == null) {
                return;
            }

            if (inItem) {
                lastItem.pubDate = content.toString();
            } else if (inChannel) {
                channel.pubDate = content.toString();
            }

            content = null;
        }

        if (localName.equalsIgnoreCase("guid")) {
            if (content == null) {
                return;
            }

            lastItem.guid = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("url")) {
            if (content == null) {
                return;
            }

            channel.imageUrl = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("width")) {
            if (content == null) {
                return;
            }

            channel.imageWidth = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("height")) {
            if (content == null) {
                return;
            }

            channel.imageHeight = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("language")) {
            if (content == null) {
                return;
            }

            channel.language = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("copyright")) {
            if (content == null) {
                return;
            }

            channel.copyright = content.toString();
            content = null;
        }

        if (localName.equalsIgnoreCase("ttl")) {
            if (content == null) {
                return;
            }

            channel.ttl = content.toString();
            content = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (content == null) {
            return;
        }

        content.append(ch, start, length);
    }

    public int getBounds() {
        return items.size();
    }

    public Item getItem(int index) {
        return items.get(index);
    }
}