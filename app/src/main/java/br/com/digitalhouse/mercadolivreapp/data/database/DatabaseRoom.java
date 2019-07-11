package br.com.digitalhouse.mercadolivreapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.digitalhouse.mercadolivreapp.data.database.dao.ResultsDao;
import br.com.digitalhouse.mercadolivreapp.model.Result;


@Database(entities = {Result.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class) // Adicionamos os conversores
public abstract class DatabaseRoom extends RoomDatabase {

    // Criamos o DAO que será retornado
    public abstract ResultsDao resultsDAO();

    private static volatile DatabaseRoom INSTANCE;

    public static DatabaseRoom getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseRoom.class, "mercadolivre_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
