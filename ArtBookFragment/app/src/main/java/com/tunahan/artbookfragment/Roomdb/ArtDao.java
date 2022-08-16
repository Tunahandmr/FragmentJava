package com.tunahan.artbookfragment.Roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tunahan.artbookfragment.Model.Art;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {

    @Query("SELECT name,id FROM Art")
    Flowable<List<Art>>  getArtWithNameAndId();

    @Query("SELECT * FROM Art WHERE id= :id")
    Flowable<Art> getArtBYId(int id);

    @Insert
    Completable insert(Art art);

}
