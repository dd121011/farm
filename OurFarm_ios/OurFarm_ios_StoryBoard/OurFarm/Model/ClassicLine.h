//
//  ClassicLine.h
//  OurFarm
//
//  Created by tian hao on 13-6-21.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <Foundation/Foundation.h>
@class Summary;

@interface ClassicLine : NSObject

@property NSNumber *itineraryId;
@property (nonatomic, copy) NSString *name;//内容名称
@property (strong) NSString *itinerarySummary;
@property (strong) NSNumber *score;
@property NSNumber *hot;//热度
@property NSNumber *price;//人均消费/票价
@property (nonatomic, copy) NSString *priceInfo;//价格信息
@property (nonatomic, copy) NSString *characteristic;//特色，用$分隔
@property (nonatomic, copy) NSString *pic;//图片URL
@property (nonatomic, copy) NSString *picMap;
@property (nonatomic, copy) NSMutableArray *lines;



- (id)initWithAttributes:(NSDictionary *)attributes;

+ (void)getClassicLineSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                      requestParameter:(NSDictionary *)param;

@end
