package com.larder.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.larder.app.domain.model.ReceiptScan;
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
public final class ReceiptScanDao_Impl implements ReceiptScanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReceiptScan> __insertionAdapterOfReceiptScan;

  private final EntityDeletionOrUpdateAdapter<ReceiptScan> __updateAdapterOfReceiptScan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteScanById;

  public ReceiptScanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceiptScan = new EntityInsertionAdapter<ReceiptScan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `receipt_scans` (`id`,`householdId`,`imagePath`,`rawOcrText`,`status`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceiptScan entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getHouseholdId());
        statement.bindString(3, entity.getImagePath());
        if (entity.getRawOcrText() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRawOcrText());
        }
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfReceiptScan = new EntityDeletionOrUpdateAdapter<ReceiptScan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `receipt_scans` SET `id` = ?,`householdId` = ?,`imagePath` = ?,`rawOcrText` = ?,`status` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceiptScan entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getHouseholdId());
        statement.bindString(3, entity.getImagePath());
        if (entity.getRawOcrText() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRawOcrText());
        }
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindString(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteScanById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM receipt_scans WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertScan(final ReceiptScan scan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceiptScan.insert(scan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateScan(final ReceiptScan scan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReceiptScan.handle(scan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteScanById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteScanById.acquire();
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
          __preparedStmtOfDeleteScanById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReceiptScan>> getScansForHousehold(final String householdId) {
    final String _sql = "SELECT * FROM receipt_scans WHERE householdId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, householdId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipt_scans"}, new Callable<List<ReceiptScan>>() {
      @Override
      @NonNull
      public List<ReceiptScan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfRawOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawOcrText");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ReceiptScan> _result = new ArrayList<ReceiptScan>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceiptScan _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpRawOcrText;
            if (_cursor.isNull(_cursorIndexOfRawOcrText)) {
              _tmpRawOcrText = null;
            } else {
              _tmpRawOcrText = _cursor.getString(_cursorIndexOfRawOcrText);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ReceiptScan(_tmpId,_tmpHouseholdId,_tmpImagePath,_tmpRawOcrText,_tmpStatus,_tmpCreatedAt);
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
  public Object getPendingScans(final Continuation<? super List<ReceiptScan>> $completion) {
    final String _sql = "SELECT * FROM receipt_scans WHERE status = 'pending'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReceiptScan>>() {
      @Override
      @NonNull
      public List<ReceiptScan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHouseholdId = CursorUtil.getColumnIndexOrThrow(_cursor, "householdId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfRawOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawOcrText");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ReceiptScan> _result = new ArrayList<ReceiptScan>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceiptScan _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHouseholdId;
            _tmpHouseholdId = _cursor.getString(_cursorIndexOfHouseholdId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpRawOcrText;
            if (_cursor.isNull(_cursorIndexOfRawOcrText)) {
              _tmpRawOcrText = null;
            } else {
              _tmpRawOcrText = _cursor.getString(_cursorIndexOfRawOcrText);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ReceiptScan(_tmpId,_tmpHouseholdId,_tmpImagePath,_tmpRawOcrText,_tmpStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
