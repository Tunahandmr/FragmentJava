package com.tunahan.artbookfragment.Model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Art{

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo (name="name")
    public String name;

    @Nullable
    @ColumnInfo (name="artistname")
    public String artistname;

    @Nullable
    @ColumnInfo (name = "year")
    public String year;

    @Nullable
   @ColumnInfo (name = "image")
    public byte[] image;

    public Art(String name, @Nullable String artistname,@Nullable String year,@Nullable byte[] image) {
        this.name = name;
        this.artistname = artistname;
        this.year = year;
        this.image = image;
    }
}
