package com.larder.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.larder.app.domain.model.Item;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ItemDao_Impl implements ItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Item> __insertionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __deletionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __updateAdapterOfItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemById;

  private final SharedSQLiteStatement __preparedStmtOfClearHouseholdItems;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItem = new EntityInsertionAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `items` (`id`,`householdId`,`name`,`category`,`quantity`,`unit`,`expiryEstimate`,`sourceImagePath`,`status`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getHouseholdId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getCategory());
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindLong(7, entity.getExpiryEstimate());
        if (entity.getSourceImagePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSourceImagePath());
        }
        statement.bindString(9, entity.getStatus());
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `items` SET `id` = ?,`householdId` = ?,`name` = ?,`category` = ?,`quantity` = ?,`unit` = ?,`expiryEstimate` = ?,`sourceImagePath` = ?,`status` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getHouseholdId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getCategory());
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindLong(7, entity.getExpiryEstimate());
        if (entity.getSourceImagePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSourceImagePath());
        }
        statement.bindString(9, entity.getStatus());
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindString(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteItemById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM items WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearHouseholdItems = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM items WHERE householdId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertItem(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<Item> items, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItem(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateItem(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItemById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteItemById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearHouseholdItems(final String householdId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearHouseholdItems.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, householdId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearHouseholdItems.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Item>> getItemsForHousehold(final String householdId) {
    final String _sql = "SELECT * FROM items WHERE householdId = ? ORDER BY expiryEstimate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, householdId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpiryEstimate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEstimate");
          final int _cursorIndexOfSourceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceImagePath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final long _tmpExpiryEstimate;
            _tmpExpiryEstimate = _cursor.getLong(_cursorIndexOfExpiryEstimate);
            final String _tmpSourceImagePath;
            if (_cursor.isNull(_cursorIndexOfSourceImagePath)) {
              _tmpSourceImagePath = null;
            } else {
              _tmpSourceImagePath = _cursor.getString(_cursorIndexOfSourceImagePath);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Item(_tmpId,_tmpHouseholdId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpiryEstimate,_tmpSourceImagePath,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getItemById(final String id, final Continuation<? super Item> $completion) {
    final String _sql = "SELECT * FROM items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Item>() {
      @Override
      @Nullable
      public Item call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpiryEstimate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEstimate");
          final int _cursorIndexOfSourceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceImagePath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final Item _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final long _tmpExpiryEstimate;
            _tmpExpiryEstimate = _cursor.getLong(_cursorIndexOfExpiryEstimate);
            final String _tmpSourceImagePath;
            if (_cursor.isNull(_cursorIndexOfSourceImagePath)) {
              _tmpSourceImagePath = null;
            } else {
              _tmpSourceImagePath = _cursor.getString(_cursorIndexOfSourceImagePath);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new Item(_tmpId,_tmpHouseholdId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpiryEstimate,_tmpSourceImagePath,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Item>> getExpiringItems(final String householdId, final long thresholdMillis) {
    final String _sql = "SELECT * FROM items WHERE householdId = ? AND expiryEstimate <= ? ORDER BY expiryEstimate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, householdId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, thresholdMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpiryEstimate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEstimate");
          final int _cursorIndexOfSourceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceImagePath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final long _tmpExpiryEstimate;
            _tmpExpiryEstimate = _cursor.getLong(_cursorIndexOfExpiryEstimate);
            final String _tmpSourceImagePath;
            if (_cursor.isNull(_cursorIndexOfSourceImagePath)) {
              _tmpSourceImagePath = null;
            } else {
              _tmpSourceImagePath = _cursor.getString(_cursorIndexOfSourceImagePath);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Item(_tmpId,_tmpHouseholdId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpiryEstimate,_tmpSourceImagePath,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Item>> getLowStockItems(final String householdId, final double minQuantity) {
    final String _sql = "SELECT * FROM items WHERE householdId = ? AND quantity <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, householdId);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, minQuantity);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpiryEstimate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEstimate");
          final int _cursorIndexOfSourceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceImagePath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final long _tmpExpiryEstimate;
            _tmpExpiryEstimate = _cursor.getLong(_cursorIndexOfExpiryEstimate);
            final String _tmpSourceImagePath;
            if (_cursor.isNull(_cursorIndexOfSourceImagePath)) {
              _tmpSourceImagePath = null;
            } else {
              _tmpSourceImagePath = _cursor.getString(_cursorIndexOfSourceImagePath);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Item(_tmpId,_tmpHouseholdId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpiryEstimate,_tmpSourceImagePath,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Item>> getItemsNeedingReview(final String householdId) {
    final String _sql = "SELECT * FROM items WHERE householdId = ? AND status = 'NEEDS_REVIEW'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, householdId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfExpiryEstimate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEstimate");
          final int _cursorIndexOfSourceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceImagePath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final long _tmpExpiryEstimate;
            _tmpExpiryEstimate = _cursor.getLong(_cursorIndexOfExpiryEstimate);
            final String _tmpSourceImagePath;
            if (_cursor.isNull(_cursorIndexOfSourceImagePath)) {
              _tmpSourceImagePath = null;
            } else {
              _tmpSourceImagePath = _cursor.getString(_cursorIndexOfSourceImagePath);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Item(_tmpId,_tmpHouseholdId,_tmpName,_tmpCategory,_tmpQuantity,_tmpUnit,_tmpExpiryEstimate,_tmpSourceImagePath,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
