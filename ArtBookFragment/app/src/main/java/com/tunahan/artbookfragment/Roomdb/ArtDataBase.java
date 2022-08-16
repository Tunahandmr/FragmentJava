package com.tunahan.artbookfragment.Roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tunahan.artbookfragment.Model.Art;

@Database(entities = {Art.class},version = 1)
public abstract class ArtDataBase extends RoomDatabase {

public abstract ArtDao artDao();

}
