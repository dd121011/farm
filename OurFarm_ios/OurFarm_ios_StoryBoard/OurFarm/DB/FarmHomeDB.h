//
//  MyViewController.h
//  OurFarm_ios
//
//  数据库操作类
//
//  Created by 李 凤勇 on 13-5-28.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ThisTrip.h"
#import "Destination.h"
#import "DestinationInLocalDB.h"
#import "Summary.h"

@interface FarmHomeDB : NSObject
// 释放资源
+ (void) finalizeStatements;

// 打开数据库（单例）
+ (id) singleton;

#pragma mark - 个人收藏操作
//添加到个人收藏
+ (BOOL) addFavorite: (int) destinationId;
//获取所有收藏数据
+ (NSMutableArray *) findAllFavorite;
//删除收藏数据
+ (void) deleteFavorite: (long) destinationId;

#pragma mark - 本次行程操作
//添加本次行程数据 sort取最大值
+ (BOOL) addThisTrip: (int) destinationId;
//添加本次行程数据
+ (void) addThisTrip: (NSInteger) destination_id atSort: (NSInteger) sort;
//删除本次行程数据 修改sort值
+ (void) updateThisTripAfterDelete: (int) destinationId;
//修改本次行程数据
+ (void) updateThisTrip: (int) destinationId atSort: (int) sort;
//移动后，更新移动后的sort值
+ (void) updateThisTripSwapSort: (int) fromSort toSort:(int) sort;
//移动后，更新移动区间的sort值
+ (void) updateThisTripBetweenSwap:(int) fromSort toSort:(int) sort moveWay:(int) upOrDown;
//删除本次行程数据 
+ (void) deleteThisTrip: (long) destinationId;
// 获取所有本次行程数据
+ (NSMutableArray *) findAllThisTrip;
// 获取单个行程数据
+ (ThisTrip *) findThisTripById: (NSString *) destinationId;
//清除缓存
+ (void) cleanCatche;

#pragma mark - 本地存储景点操作
// 获取单个景点数据
+ (DestinationInLocalDB *) findDestinationById: (NSString *) destinationId;
// 添加景点数据
+ (BOOL) addDestination: (Destination *) destination destinationJSON:(NSDictionary *)json;

@end
