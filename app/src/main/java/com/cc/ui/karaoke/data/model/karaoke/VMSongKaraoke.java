package com.cc.ui.karaoke.data.model.karaoke;

import java.io.Serializable;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongKaraoke implements Serializable {
    private long pk;
    private long svol;
    private byte ent;
    private byte opt;

    private String rowid;
    private String sabbr;
    private String sLanguage;
    private String sLyric;
    private String sLyricClean;
    private String sManufacture;
    private String sMeta;
    private String sMetaClean;
    private String sName;
    private String sNameClean;
    private String sFavorite;
    private String sYoutube;

    public VMSongKaraoke() {
    }



    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getSvol() {
        return svol;
    }

    public void setSvol(long svol) {
        this.svol = svol;
    }

    public byte getEnt() {
        return ent;
    }

    public void setEnt(byte ent) {
        this.ent = ent;
    }

    public byte getOpt() {
        return opt;
    }

    public void setOpt(byte opt) {
        this.opt = opt;
    }

    public String getSabbr() {
        return sabbr;
    }

    public void setSabbr(String sabbr) {
        this.sabbr = sabbr;
    }

    public String getsLanguage() {
        return sLanguage;
    }

    public void setsLanguage(String sLanguage) {
        this.sLanguage = sLanguage;
    }

    public String getsLyric() {
        return sLyric;
    }

    public void setsLyric(String sLyric) {
        this.sLyric = sLyric;
    }

    public String getsLyricClean() {
        return sLyricClean;
    }

    public void setsLyricClean(String sLyricClean) {
        this.sLyricClean = sLyricClean;
    }

    public String getsManufacture() {
        return sManufacture;
    }

    public void setsManufacture(String sManufacture) {
        this.sManufacture = sManufacture;
    }

    public String getsMeta() {
        return sMeta;
    }

    public void setsMeta(String sMeta) {
        this.sMeta = sMeta;
    }

    public String getsMetaClean() {
        return sMetaClean;
    }

    public void setsMetaClean(String sMetaClean) {
        this.sMetaClean = sMetaClean;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsNameClean() {
        return sNameClean;
    }

    public void setsNameClean(String sNameClean) {
        this.sNameClean = sNameClean;
    }

    public String getsFavorite() {
        return sFavorite;
    }

    public void setsFavorite(String sFavorite) {
        this.sFavorite = sFavorite;
    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getsYoutube() {
        return sYoutube;
    }

    public void setsYoutube(String sYoutube) {
        this.sYoutube = sYoutube;
    }
}
