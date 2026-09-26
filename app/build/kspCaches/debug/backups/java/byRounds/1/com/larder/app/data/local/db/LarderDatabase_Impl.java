package com.larder.app.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.larder.app.data.local.dao.HouseholdDao;
import com.larder.app.data.local.dao.HouseholdDao_Impl;
import com.larder.app.data.local.dao.ItemDao;
import com.larder.app.data.local.dao.ItemDao_Impl;
import com.larder.app.data.local.dao.ReceiptScanDao;
import com.larder.app.data.local.dao.ReceiptScanDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LarderDatabase_Impl extends LarderDatabase {
  private volatile ItemDao _itemDao;

  private volatile ReceiptScanDao _receiptScanDao;

  private volatile HouseholdDao _householdDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `households` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `household_members` (`householdId` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT NOT NULL, PRIMARY KEY(`householdId`, `userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `items` (`id` TEXT NOT NULL, `householdId` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `expiryEstimate` INTEGER NOT NULL, `sourceImagePath` TEXT, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `receipt_scans` (`id` TEXT NOT NULL, `householdId` TEXT NOT NULL, `imagePath` TEXT NOT NULL, `rawOcrText` TEXT, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c21355cfde8b7639f8ae8f42486e134b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `households`");
        db.execSQL("DROP TABLE IF EXISTS `household_members`");
        db.execSQL("DROP TABLE IF EXISTS `items`");
        db.execSQL("DROP TABLE IF EXISTS `receipt_scans`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsHouseholds = new HashMap<String, TableInfo.Column>(3);
        _columnsHouseholds.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHouseholds.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHouseholds.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHouseholds = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHouseholds = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHouseholds = new TableInfo("households", _columnsHouseholds, _foreignKeysHouseholds, _indicesHouseholds);
        final TableInfo _existingHouseholds = TableInfo.read(db, "households");
        if (!_infoHouseholds.equals(_existingHouseholds)) {
          return new RoomOpenHelper.ValidationResult(false, "households(com.larder.app.domain.model.Household).\n"
                  + " Expected:\n" + _infoHouseholds + "\n"
                  + " Found:\n" + _existingHouseholds);
        }
        final HashMap<String, TableInfo.Column> _columnsHouseholdMembers = new HashMap<String, TableInfo.Column>(3);
        _columnsHouseholdMembers.put("householdId", new TableInfo.Column("householdId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHouseholdMembers.put("userId", new TableInfo.Column("userId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHouseholdMembers.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHouseholdMembers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHouseholdMembers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHouseholdMembers = new TableInfo("household_members", _columnsHouseholdMembers, _foreignKeysHouseholdMembers, _indicesHouseholdMembers);
        final TableInfo _existingHouseholdMembers = TableInfo.read(db, "household_members");
        if (!_infoHouseholdMembers.equals(_existingHouseholdMembers)) {
          return new RoomOpenHelper.ValidationResult(false, "household_members(com.larder.app.domain.model.HouseholdMember).\n"
                  + " Expected:\n" + _infoHouseholdMembers + "\n"
                  + " Found:\n" + _existingHouseholdMembers);
        }
        final HashMap<String, TableInfo.Column> _columnsItems = new HashMap<String, TableInfo.Column>(11);
        _columnsItems.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("householdId", new TableInfo.Column("householdId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("expiryEstimate", new TableInfo.Column("expiryEstimate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("sourceImagePath", new TableInfo.Column("sourceImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoItems = new TableInfo("items", _columnsItems, _foreignKeysItems, _indicesItems);
        final TableInfo _existingItems = TableInfo.read(db, "items");
        if (!_infoItems.equals(_existingItems)) {
          return new RoomOpenHelper.ValidationResult(false, "items(com.larder.app.domain.model.Item).\n"
                  + " Expected:\n" + _infoItems + "\n"
                  + " Found:\n" + _existingItems);
        }
        final HashMap<String, TableInfo.Column> _columnsReceiptScans = new HashMap<String, TableInfo.Column>(6);
        _columnsReceiptScans.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceiptScans.put("householdId", new TableInfo.Column("householdId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceiptScans.put("imagePath", new TableInfo.Column("imagePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceiptScans.put("rawOcrText", new TableInfo.Column("rawOcrText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceiptScans.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceiptScans.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceiptScans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceiptScans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceiptScans = new TableInfo("receipt_scans", _columnsReceiptScans, _foreignKeysReceiptScans, _indicesReceiptScans);
        final TableInfo _existingReceiptScans = TableInfo.read(db, "receipt_scans");
        if (!_infoReceiptScans.equals(_existingReceiptScans)) {
          return new RoomOpenHelper.ValidationResult(false, "receipt_scans(com.larder.app.domain.model.ReceiptScan).\n"
                  + " Expected:\n" + _infoReceiptScans + "\n"
                  + " Found:\n" + _existingReceiptScans);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c21355cfde8b7639f8ae8f42486e134b", "e8c56ddb403ef262c80ff1a377e07adc");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "households","household_members","items","receipt_scans");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `households`");
      _db.execSQL("DELETE FROM `household_members`");
      _db.execSQL("DELETE FROM `items`");
      _db.execSQL("DELETE FROM `receipt_scans`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ItemDao.class, ItemDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReceiptScanDao.class, ReceiptScanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HouseholdDao.class, HouseholdDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ItemDao itemDao() {
    if (_itemDao != null) {
      return _itemDao;
    } else {
      synchronized(this) {
        if(_itemDao == null) {
          _itemDao = new ItemDao_Impl(this);
        }
        return _itemDao;
      }
    }
  }

  @Override
  public ReceiptScanDao receiptScanDao() {
    if (_receiptScanDao != null) {
      return _receiptScanDao;
    } else {
      synchronized(this) {
        if(_receiptScanDao == null) {
          _receiptScanDao = new ReceiptScanDao_Impl(this);
        }
        return _receiptScanDao;
      }
    }
  }

  @Override
  public HouseholdDao householdDao() {
    if (_householdDao != null) {
      return _householdDao;
    } else {
      synchronized(this) {
        if(_householdDao == null) {
          _householdDao = new HouseholdDao_Impl(this);
        }
        return _householdDao;
      }
    }
  }
}
