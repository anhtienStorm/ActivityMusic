package com.example.activitymusic;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AllSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Song> songList = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            int indexIdColumn = data.getColumnIndex(MediaStore.Audio.Media._ID);
            int indexTitleColumn = data.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexDataColumn = data.getColumnIndex(MediaStore.Audio.Media.DATA);
            int indexArtistColumn = data.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexAlbumIDColumn = data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int indexDurationColumn = data.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                int id = Integer.parseInt(data.getString(indexIdColumn));
                String title = data.getString(indexTitleColumn);
                String path = data.getString(indexDataColumn);
                String artist = data.getString(indexArtistColumn);
                String albumID = data.getString(indexAlbumIDColumn);
                int duration = Integer.parseInt(data.getString(indexDurationColumn));
                SimpleDateFormat formatTimeSong = new SimpleDateFormat("mm:ss");
                String timeSong = formatTimeSong.format(duration);
                Song song = new Song(id, title, path, artist, albumID, timeSong);
                songList.add(song);

                if (!checkIdExitFavoriteSongs(id)){
                    addIdProviderForFavoriteSongsList(id);
                }

            } while (data.moveToNext());
        }
        mAdapter.updateList(songList);
        setListSong(songList);
        mAdapter.setTypeSongList("AllSongs");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.updateList(new ArrayList<Song>());
        }
    }

    public ArrayList<Integer> loadIdProviderFromFavoriteSongs(){
        ArrayList<Integer> listId = new ArrayList<>();
        Cursor c = getActivity().getContentResolver().query(FavoriteSongsProvider.CONTENT_URI, null, null, null, null);
        if (c.moveToFirst()){
            do {
                int id = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER)));
                listId.add(id);
            } while (c.moveToNext());
        }
        return listId;
    }

    public boolean checkIdExitFavoriteSongs(int id){
        ArrayList<Integer> list = loadIdProviderFromFavoriteSongs();
        if (list.contains(id))
            return true;
        else
            return false;
    }

    public void addIdProviderForFavoriteSongsList(int id) {
        ContentValues values = new ContentValues();

        values.put(FavoriteSongsProvider.ID_PROVIDER,
                id);

        Uri uri = getActivity().getContentResolver().insert(
                FavoriteSongsProvider.CONTENT_URI, values);
        Toast.makeText(getActivity(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

}
