//
//  MyViewController.h
//  OurFarm_ios
//
//  数据库操作类
//
//  Created by 李 凤勇 on 13-5-28.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import "FarmHomeDB.h"
#import "SQLiteHelper.h"
#import "DestinationInLocalDB.h"

#import "SBJsonWriter.h"

@implementation FarmHomeDB

// 数据库连接
static sqlite3 *kFarmHomeDatabase;
// SQLite帮助类
static SQLiteHelper *kSqlite;

// 获取收藏
static sqlite3_stmt *fetchFavoriteStatement = nil;
// 获取行程
static sqlite3_stmt *fetchThisTripStatement = nil;
// 根据Id获取一条行程纪录
static sqlite3_stmt *fetchThisTripByIdStatement = nil;
// 根据Id获取一条景点纪录
static sqlite3_stmt *fetchDestinationByIdStatement = nil;

#pragma mark - 数据库资源操作
// 释放资源
+ (void) finalizeStatements
{
	if (fetchThisTripStatement)
	{
		sqlite3_finalize(fetchThisTripStatement);
	}
    if (fetchThisTripByIdStatement)
	{
		sqlite3_finalize(fetchThisTripByIdStatement);
	}
    
    [kSqlite closeDatabase];
}

// 打开数据库（单例）
+ (id) singleton
{
	return [[self alloc] init];
}

-(id) init
{
	if ((self=[super init]) ) {
		if (kFarmHomeDatabase == nil)
		{
			if (kSqlite == nil) 
            {
				kSqlite = [[SQLiteHelper alloc] init];
			}
            
            [kSqlite createEditableCopyOfDatabaseIfNeeded];
			[kSqlite initDatabaseConnection];
			
			kFarmHomeDatabase = [kSqlite database];
		}
	}
	
	return self;
}

#pragma mark - 个人收藏操作
/**
 * 添加到个人收藏
 *
 * @param destinationId 景点Id
 */
+ (BOOL) addFavorite: (int) destinationId {
    sqlite3_stmt *statement;
    
    static char *sql = "INSERT OR REPLACE INTO favorite (destinationId) VALUES (?)";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }
    
    sqlite3_bind_int(statement, 1, destinationId);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to add ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }
    
    sqlite3_finalize(statement);
    
    return TRUE;
}

/**
 * 获取所有收藏数据
 */
+ (NSMutableArray *) findAllFavorite {
    NSMutableArray *deslist = [NSMutableArray array];
    
	// Compile the query for retrieving data.
	if (fetchFavoriteStatement == nil) {
        /** 获取本次行程的景点 **/
        const char *sql =
        "select des.* from favorite fav,destination des where fav.destinationId = des.destination_id ";
        
		if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &fetchFavoriteStatement, NULL) != SQLITE_OK) {
			NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
		}
	}
	
	while (sqlite3_step(fetchFavoriteStatement) == SQLITE_ROW) {
		DestinationInLocalDB *destinationInDB = [[DestinationInLocalDB alloc] init];
        
        destinationInDB.destination_id = sqlite3_column_int(fetchFavoriteStatement, 0);
        destinationInDB.json = [NSString stringWithUTF8String:(char *)sqlite3_column_text(fetchFavoriteStatement, 1)];
		
		[deslist addObject:destinationInDB];
	}
	
	// Reset the statement for future reuse.
	sqlite3_reset(fetchFavoriteStatement);
    
    return deslist;
}

/**
 * 删除收藏数据
 *
 * @param destinationId 景点Id
 */
+ (void) deleteFavorite: (long) destinationId {
    sqlite3_stmt *statement;
    
    static char *sql = "delete from favorite where destinationId = ?";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_bind_int(statement, 1, destinationId);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to delete album with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

#pragma mark - 本次行程操作

/**
 * 添加本次行程数据 sort取最大值
 *
 * @param destinationId 景点Id
 */
+ (BOOL) addThisTrip: (int) destinationId{
    
    sqlite3_stmt *statement;
    sqlite3_stmt *fetchThisTripMaxSort = nil;
    int sort = 0;

    // Compile the query for retrieving data.
    if (fetchThisTripMaxSort == nil) {
        const char *sql = "select max(sort) from this_trip";
        if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &fetchThisTripMaxSort, NULL) != SQLITE_OK) {
            NSAssert1(0, @"Error: failed to prepare select max(sort) from this_trip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
            return FALSE;
        }
    }
    
    while (sqlite3_step(fetchThisTripMaxSort) == SQLITE_ROW)
    {
        sort = sqlite3_column_int(fetchThisTripMaxSort, 0);
    }
    
    // Reset the statement for future reuse.
    sqlite3_reset(fetchThisTripMaxSort);
    
    static char *sql = "INSERT OR REPLACE INTO this_trip (destinationId,sort) VALUES (?,?)";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }
    
    sqlite3_bind_int(statement, 1, destinationId);
    sqlite3_bind_int(statement, 2, sort + 1);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to add ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }
    
    sqlite3_finalize(statement);
    
    return TRUE;
}

/**
 * 添加本次行程数据
 *
 * @param destinationId 景点Id 
 * @param sort 排名
 */
+ (void) addThisTrip: (int) destinationId atSort: (int) sort{
    sqlite3_stmt *statement;
    
    static char *sql = "INSERT OR REPLACE INTO this_trip (destinationId,sort) VALUES (?,?)";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_bind_int(statement, 1, destinationId);
    sqlite3_bind_int(statement, 2, sort);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to add ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

/**
 * 删除本次行程数据 修改sort值
 *
 * @param destinationId 景点Id
 */
+ (void) updateThisTripAfterDelete: (int) destinationId {
    sqlite3_stmt *statement;
    
    static char *sql = "UPDATE this_trip set sort = sort - 1 WHERE sort > (select sort from this_trip where destinationId = ?)";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    sqlite3_bind_int(statement, 1, destinationId);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to edit ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}


/**
 * 修改本次行程数据
 *
 * @param destinationId 景点Id
 * @param sort 排名
 */
+ (void) updateThisTrip: (int) destinationId atSort: (int) sort {
    sqlite3_stmt *statement;
    
    static char *sql = "UPDATE this_trip set sort = ? WHERE destinationId = ?";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    sqlite3_bind_int(statement, 1, sort);
    sqlite3_bind_int(statement, 2, destinationId);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to edit ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

/**
 * 移动后，更新移动后的sort值
 *
 * @param fromSort 移动的sort值
 * @param toSort 移动到那个位置的sort值
 */
+ (void) updateThisTripSwapSort: (int) fromSort toSort:(int) sort {
    sqlite3_stmt *statement;
    
    static char *sql = "UPDATE this_trip set sort = ? WHERE sort = ?";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    sqlite3_bind_int(statement, 1, sort);
    sqlite3_bind_int(statement, 2, fromSort);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to edit ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

/**
 * 移动后，更新移动区间的sort值
 *
 * @param fromSort 移动的sort值
 * @param toSort 移动到那个位置的sort值
 * @param moveWay 移动方式，－1:向下 1:向上
 */
+ (void) updateThisTripBetweenSwap:(int) fromSort toSort:(int) sort moveWay:(int) upOrDown {
    sqlite3_stmt *statement;
    
    static char *sql = nil;
    if (upOrDown == -1) {
        sql = "UPDATE this_trip set sort = sort - 1 WHERE sort > ? and sort <= ?";
    } else if(upOrDown == 1) {
        sql = "UPDATE this_trip set sort = sort + 1 WHERE sort < ? and sort >= ?";
    }
    
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    sqlite3_bind_int(statement, 1, fromSort);
    sqlite3_bind_int(statement, 2, sort);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to edit ThisTrip with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

/**
 * 删除本次行程数据
 *
 * @param destinationId 景点Id
 */
+ (void) deleteThisTrip: (long) destinationId
{
    sqlite3_stmt *statement;
    
    static char *sql = "delete from this_trip where destinationId = ?";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_bind_int(statement, 1, destinationId);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to delete album with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

//update this_trip set sort = sort -1  where sort in (select sort from this_trip  order by sort limit 2,3);

/**
 * 查找本次行程信息
 */
+ (NSMutableArray *) findAllThisTrip
{
    NSMutableArray *deslist = [NSMutableArray array];
    
	// Compile the query for retrieving data.
	if (fetchThisTripStatement == nil) {
        /** 获取本次行程的景点 **/
        const char *sql = 
        "select des.* from this_trip tt,destination des where tt.destinationId = des.destination_id order by sort;";
        
		if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &fetchThisTripStatement, NULL) != SQLITE_OK) {
			NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
		}
	}
	
	while (sqlite3_step(fetchThisTripStatement) == SQLITE_ROW) {
		DestinationInLocalDB *destinationInDB = [[DestinationInLocalDB alloc] init];
        
        destinationInDB.destination_id = sqlite3_column_int(fetchThisTripStatement, 0);
        destinationInDB.json = [NSString stringWithUTF8String:(char *)sqlite3_column_text(fetchThisTripStatement, 1)];
		
		[deslist addObject:destinationInDB];
	}
	
	// Reset the statement for future reuse.
	sqlite3_reset(fetchThisTripStatement);
    
    return deslist;
}

// 获取单个行程数据
+ (ThisTrip *) findThisTripById: (NSString *) destinationId {
    ThisTrip *thisTrip = nil;
    // Compile the query for retrieving data.
    if (fetchThisTripByIdStatement == nil) {
        const char *sql = "select destinationId, sort from this_trip where destinationId = ?";
        if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &fetchThisTripByIdStatement, NULL) != SQLITE_OK) {
            NSAssert1(0, @"Error: failed to prepare fetchThisTripByIdStatement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
            return nil;
        }
    }
    sqlite3_bind_text(fetchDestinationByIdStatement, 1, [destinationId UTF8String], -1, SQLITE_TRANSIENT);
    
    while (sqlite3_step(fetchThisTripByIdStatement) == SQLITE_ROW)
    {
        thisTrip = [[ThisTrip alloc] init];
        thisTrip.destinationId = sqlite3_column_int(fetchThisTripByIdStatement, 0);
        thisTrip.sort = sqlite3_column_int(fetchThisTripByIdStatement, 1);
    }
    
    // Reset the statement for future reuse.
    sqlite3_reset(fetchThisTripByIdStatement);
    
    return thisTrip;
}

#pragma mark - 本地存储景点操作
// 获取单个景点数据
+ (DestinationInLocalDB *) findDestinationById: (NSString *) destinationId {
    DestinationInLocalDB *destinationLocal = nil;
    // Compile the query for retrieving data.
    if (fetchDestinationByIdStatement == nil) {
        const char *sql = "select * from destination where destination_id = ?";
        if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &fetchDestinationByIdStatement, NULL) != SQLITE_OK) {
            NSAssert1(0, @"Error: failed to prepare findDestinationById with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        }
    }
    //sqlite3_bind_int(fetchDestinationByIdStatement, 1, destinationId);
    sqlite3_bind_text(fetchDestinationByIdStatement, 1, [destinationId UTF8String], -1, SQLITE_TRANSIENT);
    
    while (sqlite3_step(fetchDestinationByIdStatement) == SQLITE_ROW) {
        destinationLocal = [[DestinationInLocalDB alloc] init];
        destinationLocal.destination_id = sqlite3_column_int(fetchDestinationByIdStatement, 0);
        destinationLocal.json = [NSString stringWithUTF8String: (char *)sqlite3_column_text(fetchDestinationByIdStatement, 1)];
        //NSLog(@"json: %@", destinationLocal.json);
    }
    
    // Reset the statement for future reuse.
    sqlite3_reset(fetchDestinationByIdStatement);
    
    return destinationLocal;
}

/**
 * 添加景点数据
 *
 * @param Destination 景点
 */
+ (BOOL) addDestination: (Destination *) destination destinationJSON:(NSDictionary *)json{
    sqlite3_stmt *statement;
    
    static char *sql = "INSERT OR REPLACE INTO destination (destination_id, json, cache, lat, lng, type) VALUES (?,?,?,?,?,?)";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK) {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }

    SBJsonWriter *sbJsonWriter = [[SBJsonWriter alloc] init];
    Summary *summary = destination.summary;
    //JSON --> String
    NSString *destinationJson = [sbJsonWriter stringWithObject:json];
    
    NSLog(@"json-->string: %@", destinationJson);
    
    sqlite3_bind_int(statement, 1, [summary.destinationId intValue]);
    sqlite3_bind_text(statement, 2, [destinationJson UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(statement, 3, [@"TRUE" UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_double(statement, 4, [summary.lat doubleValue]);
    sqlite3_bind_double(statement, 5, [summary.lng doubleValue]);
    sqlite3_bind_int(statement, 6, 1);
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to add destination with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
        return FALSE;
    }
    
    sqlite3_finalize(statement);
    
    return TRUE;
}

//清除缓存
+ (void) cleanCatche {
    [self deleteImageCatche];
    [self deleteDestination];
}

/**
 * 删除收藏数据
 *
 * @param destinationId 景点Id
 */
+ (void) deleteImageCatche{
    sqlite3_stmt *statement;
    
    static char *sql = "delete from image where cache = 1";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
        
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to delete image with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

/**
 * 删除收藏数据
 *
 * @param destinationId 景点Id
 */
+ (void) deleteDestination{
    sqlite3_stmt *statement;
    
    static char *sql = "delete from destination where cache = 1";
    if (sqlite3_prepare_v2(kFarmHomeDatabase, sql, -1, &statement, NULL) != SQLITE_OK)
    {
        NSAssert1(0, @"Error: failed to prepare statement with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    int success = sqlite3_step(statement);
    
    if (success == SQLITE_ERROR)
    {
        NSAssert1(0, @"Error: failed to delete image with message '%s'.", sqlite3_errmsg(kFarmHomeDatabase));
    }
    
    sqlite3_finalize(statement);
}

@end
